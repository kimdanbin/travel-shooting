package com.example.travelshooting.reservation.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.DomainType;
import com.example.travelshooting.enums.NotificationStatus;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.dto.NotificationDetails;
import com.example.travelshooting.notification.entity.Notification;
import com.example.travelshooting.notification.service.NotificationService;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ReservationMailService reservationMailService;
    private final PartService partService;
    private final NotificationService notificationService;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private static final String CACHE_KEY_PREFIX = "reservations:product:";

    @Transactional(readOnly = true)
    public List<ReservationResDto> findAllByProductIdAndUserId(Long productId, Pageable pageable) {

        final String cacheKey = CACHE_KEY_PREFIX + productId + ":page:" + pageable.getPageNumber();

        // 첫 번째 페이지일 경우 캐시에서 조회
        if (pageable.getPageNumber() == 0) {
            @SuppressWarnings("unchecked")
            List<ReservationResDto> cachedReservations = (List<ReservationResDto>) redisObjectTemplate.opsForValue().get(cacheKey);
            if (cachedReservations != null) {
                log.info("캐시에서 예약 첫 번째 페이지 조회: {}", cacheKey);
                return cachedReservations;
            }
        }

        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);

        Page<Reservation> reservations = reservationRepository.findAllByProductIdAndUserId(product.getId(), user.getId(), pageable);

        if (reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        List<ReservationResDto> result = reservations.stream().map(reservation -> new ReservationResDto(
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
        )).collect(Collectors.toList());

        // 첫 번째 페이지일 경우 캐시에 저장
        if (pageable.getPageNumber() == 0) {
            redisObjectTemplate.opsForValue().set(cacheKey, result, Const.RESERVATION_CASH_TIMEOUT, TimeUnit.MINUTES);
            log.info("첫 번째 페이지 캐시 저장: {}", cacheKey);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ReservationResDto findReservationByProductIdAndUserIdAndId(Long productId, Long reservationId) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Reservation reservation = reservationRepository.findReservationByProductIdAndUserIdAndId(product.getId(), user.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
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

    @Transactional
    public ReservationResDto updateReservationStatus(Long productId, Long reservationId, String status) {
        User partner = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Reservation reservation = reservationRepository.findReservationByProductIdAndUserIdAndId(product.getId(), partner.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        Part part = partService.findPartByReservationId(reservation.getId());
        User user = userService.findUserByReservationId(reservation.getId());

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 수락 또는 거절 상태입니다.");
        }

        reservation.updateStatus(ReservationStatus.valueOf(status));
        Reservation updatedReservation = reservationRepository.save(reservation);

        // 메일
        reservationMailService.sendMail(user, product, part, reservation, reservation.getStatus());

        // 알림
        Map<ReservationStatus, NotificationDetails> detailsMap = notificationService.reservationDetails();
        NotificationDetails details = detailsMap.get(reservation.getStatus());
        notificationService.save(new Notification(user, DomainType.RESERVATION, reservation.getId(), details.subject(), NotificationStatus.SENT, details.type()));

        // 상태 업데이트 시 첫 번째 페이지 캐시 삭제
        final String cacheKey = CACHE_KEY_PREFIX + productId + ":page:0";
        redisObjectTemplate.delete(cacheKey);
        log.info("예약 업데이트 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);

        return new ReservationResDto(
                updatedReservation.getId(),
                updatedReservation.getUser().getId(),
                product.getId(),
                updatedReservation.getPart().getId(),
                updatedReservation.getReservationDate(),
                updatedReservation.getHeadCount(),
                updatedReservation.getTotalPrice(),
                updatedReservation.getStatus(),
                updatedReservation.getCreatedAt(),
                updatedReservation.getUpdatedAt()
        );
    }
}
