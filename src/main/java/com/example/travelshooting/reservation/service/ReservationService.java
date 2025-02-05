package com.example.travelshooting.reservation.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.config.util.CacheKeyUtil;
import com.example.travelshooting.config.util.LockKeyUtil;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.notification.service.SendEmailEvent;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final PartService partService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final RedissonClient redissonClient;

    @Transactional
    public ReservationResDto createReservation(Long productId, Long partId, LocalDate reservationDate, Integer headCount) {
        Part part = partService.findPartByIdAndProductId(partId, productId);

        if (part == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품에 대한 일정이 존재하지 않습니다.");
        }

        String lockKey = LockKeyUtil.getReservationLockKey(reservationDate, part.getId());
        RLock lock = redissonClient.getLock(lockKey);
        log.info("lock 시도: {}", lockKey);

        try {
            boolean available = lock.tryLock(Const.RESERVATION_LOCK_WAIT_TIME, Const.RESERVATION_LOCK_LEASE_TIME, TimeUnit.SECONDS);

            if (!available) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 예약 요청이 많아 다시 시도해 주세요.");
            }

            log.info("lock 획득 성공: {}", lockKey);
            return processReservation(part.getProduct(), part, reservationDate, headCount);
        } catch (InterruptedException e) {
            throw new RuntimeException("예약 도중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("unlock 성공: {}", lock.getName());
            } else {
                log.warn("unlock 시도: lock을 소유하지 않음: {}", lock.getName());
            }
        }
    }

    @Transactional
    public ReservationResDto processReservation(Product product, Part part, LocalDate reservationDate, Integer headCount) {
        User user = userService.findAuthenticatedUser();
        boolean isReservation = reservationRepository.existsReservationByUserIdAndReservationDateAndIsDeleted(user.getId(), reservationDate, false);
        Integer totalHeadCount = reservationRepository.findTotalHeadCountByPartIdAndReservationDate(part.getId(), reservationDate);

        validCreateReservation(reservationDate, headCount, isReservation, product, part, totalHeadCount, user);

        Integer totalPrice = product.getPrice() * headCount;

        Reservation reservation = new Reservation(user, part, reservationDate, headCount, totalPrice);
        reservationRepository.save(reservation);

        try {
            eventPublisher.publishEvent(new SendEmailEvent(this, reservation));
        } catch (Exception e) {
            log.warn("메일 전송을 실패하였습니다.");
        }

        return new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                product.getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getHeadCount(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<ReservationResDto> findAllByUserIdAndProductId(Long productId, Pageable pageable) {
        User authenticatedUser = userService.findAuthenticatedUser();

        return reservationRepository.findAllByUserIdAndProductId(productId, authenticatedUser, pageable);
    }

    @Transactional(readOnly = true)
    public ReservationResDto findReservationByProductIdAndId(Long productId, Long reservationId) {
        User user = userService.findAuthenticatedUser();
        ReservationResDto reservation = reservationRepository.findReservationByProductIdAndId(productId, reservationId, user.getId());

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return reservation;
    }

    @Transactional
    public void deleteReservation(Long productId, Long reservationId) {
        findReservationByProductIdAndId(productId, reservationId);
        Reservation reservation = reservationRepository.updateStatusAndIsDeleted(reservationId, ReservationStatus.CANCELED, true);

        try {
            eventPublisher.publishEvent(new SendEmailEvent(this, reservation));
        } catch (Exception e) {
            log.warn("메일 전송을 실패하였습니다.");
        }

        // 예약 취소 시 첫 번째 페이지 캐시 삭제
        final String cacheKey = CacheKeyUtil.getReservationProductPageKey(productId, 0);;
        redisObjectTemplate.delete(cacheKey);
        log.info("예약 취소 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);
    }

    @Transactional
    public void cancelExpiredReservations() {
        List<Reservation> approvedReservations = reservationRepository.findAllByStatus(ReservationStatus.APPROVED);

        approvedReservations.forEach(reservation -> {
            boolean isPaid = reservation.getPayments().stream()
                    .anyMatch(payment -> payment.getStatus() == PaymentStatus.APPROVED);

            LocalDateTime expirationTime = reservation.getUpdatedAt().plusDays(Const.RESERVATION_EXPIRED_DAY).withHour(Const.RESERVATION_EXPIRED_HOUR).withMinute(0).withSecond(0).withNano(0);

            if (!isPaid && LocalDateTime.now().isAfter(expirationTime)) {
                reservation.updateReservation(ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);

                try {
                    eventPublisher.publishEvent(new SendEmailEvent(this, reservation));
                } catch (Exception e) {
                    log.warn("메일 전송을 실패하였습니다.");
                }
            }
        });
    }

    private static void validCreateReservation(LocalDate reservationDate, Integer headCount, boolean isReservation, Product product, Part part, Integer totalHeadCount, User user) {
        if (user.getRole().equals(UserRole.PARTNER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자만 예약할 수 있습니다.");
        }

        if (isReservation) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 날짜에 예약한 내역이 있습니다.");
        }

        if (product.getSaleStartAt().isAfter(reservationDate) || product.getSaleEndAt().isBefore(reservationDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 날짜는 상품의 판매 기간 중에서만 선택할 수 있습니다.");
        }

        if (LocalDate.now().isAfter(reservationDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지난 날짜는 예약할 수 없습니다.");
        }

        if (LocalDate.now().equals(reservationDate) && LocalTime.now().isAfter(part.getOpenAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 오픈 시간이 지나 예약이 불가능합니다.");
        }

        if (part.getMaxQuantity() < totalHeadCount + headCount) {
            Integer overHeadCount = Math.abs(part.getMaxQuantity() - totalHeadCount - headCount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신청 가능한 인원을 초과했습니다. 초과된 인원: " + overHeadCount);
        }
    }

    public Reservation findReservationByPaymentIdAndUserId(Long paymentId, Long userId) {
        return reservationRepository.findReservationByPaymentIdAndUserId(paymentId, userId);
    }

    public Reservation findReservationByProductIdAndIdAndUserId(Long productId, Long reservationId, Long userId) {
        return reservationRepository.findReservationByProductIdAndIdAndUserId(productId, reservationId, userId);
    }
}