package com.example.travelshooting.reservation.service;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.service.SendEmailEvent;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<ReservationResDto> findPartnerReservationsByProductIdAndUserId(Long productId, Pageable pageable) {
        User authenticatedUser = userService.findAuthenticatedUser();

        return reservationRepository.findPartnerReservationsByProductIdAndUserId(productId, authenticatedUser, pageable);
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
        User user = userService.findAuthenticatedUser();
        Reservation reservation = reservationRepository.findPartnerReservationByProductIdAndIdAndUserId(productId, reservationId, user);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 승인된 예약입니다.");
        }

        boolean isDeleted = ReservationStatus.REJECTED.name().equals(status);
        reservation.updateReservation(ReservationStatus.valueOf(status), isDeleted);
        reservationRepository.save(reservation);

        try {
            eventPublisher.publishEvent(new SendEmailEvent(this, reservation));
        } catch (Exception e) {
            log.warn("메일 전송을 실패하였습니다.");
        }

        return new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getPart().getProduct().getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getHeadCount(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
