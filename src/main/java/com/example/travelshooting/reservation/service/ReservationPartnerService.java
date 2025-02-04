package com.example.travelshooting.reservation.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.config.util.CacheKeyUtil;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.service.SendEmailEvent;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<ReservationResDto> findPartnerReservationsByProductIdAndUserId(Long productId, Pageable pageable) {
        User authenticatedUser = userService.findAuthenticatedUser();

        if(pageable.getPageSize() != Const.PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지 사이즈는 20 만 가능합니다.");
        }

        final String cacheKey = CacheKeyUtil.getReservationProductPageKey(productId, pageable.getPageNumber());

        // 캐시에서 데이터 가져오기
        Object cachedReservations = redisObjectTemplate.opsForValue().get(cacheKey);

        if (cachedReservations != null) {
            log.info("캐시에서 예약 첫 번째 페이지 조회: {}", cacheKey);

            // 캐시된 데이터를 Map 형태로 변환
            Map<String, Object> cachedData = objectMapper.convertValue(cachedReservations, new TypeReference<Map<String, Object>>() {
            });

            // results 필드에서 예약 리스트 추출
            List<ReservationResDto> reservations = objectMapper.convertValue(
                    cachedData.get("results"),
                    new TypeReference<List<ReservationResDto>>() {
                    } // List<ReservationResDto> 타입으로 변환
            );

            // 캐시된 데이터로 PageImpl 생성하여 반환
            return new PageImpl<>(reservations, pageable, (int) cachedData.get("total"));

        }

        Page<ReservationResDto> reservations = reservationRepository.findPartnerReservationsByProductIdAndUserId(productId, authenticatedUser, pageable);

        // 첫 번째 페이지일 경우 캐시에 저장
        if (pageable.getPageNumber() == 0) {
            redisObjectTemplate.opsForValue().set(cacheKey, reservations, Const.RESERVATION_CASH_TIMEOUT, TimeUnit.MINUTES);
            log.info("첫 번째 페이지 캐시 저장: {}", cacheKey);
        }

        return reservations;
    }

    @Transactional(readOnly = true)
    public ReservationResDto findPartnerReservationByProductIdAndId(Long productId, Long reservationId) {
        User authenticatedUser = userService.findAuthenticatedUser();
        ReservationResDto reservation = reservationRepository.findPartnerReservationByProductIdAndId(productId, reservationId, authenticatedUser);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return reservation;
    }

    @Transactional
    public ReservationResDto updateReservationStatus(Long productId, Long reservationId, String status) {
        ReservationResDto reservation = findPartnerReservationByProductIdAndId(productId, reservationId);

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 승인된 예약입니다.");
        }

        boolean isDeleted = ReservationStatus.REJECTED.name().equals(status);
        Reservation updatedReservation = reservationRepository.updateStatusAndIsDeleted(reservationId, ReservationStatus.valueOf(status), isDeleted);

        try {
            eventPublisher.publishEvent(new SendEmailEvent(this, updatedReservation));
        } catch (Exception e) {
            log.warn("메일 전송을 실패하였습니다.");
        }

        // 상태 업데이트 시 첫 번째 페이지 캐시 삭제
        final String cacheKey = CacheKeyUtil.getReservationProductPageKey(productId, 0);
        redisObjectTemplate.delete(cacheKey);
        log.info("예약 업데이트 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);

        return new ReservationResDto(
                updatedReservation.getId(),
                updatedReservation.getUser().getId(),
                updatedReservation.getPart().getProduct().getId(),
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
