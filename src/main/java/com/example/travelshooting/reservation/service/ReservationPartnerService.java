package com.example.travelshooting.reservation.service;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ReservationResDto> findByProductIdAndUserId(Long productId) {
        User user = userService.getAuthenticatedUser();
        List<Reservation> reservations = reservationRepository.findByProductIdAndUserId(productId, user.getId());

        if (reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return reservations.stream().map(reservation -> new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getProduct().getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getNumber(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResDto findByProductIdAndUserIdAndId(Long productId, Long reservationId) {
        User user = userService.getAuthenticatedUser();
        Reservation reservation = reservationRepository.findByProductIdAndUserIdAndId(productId, user.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getProduct().getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getNumber(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
                );
    }

    @Transactional
    public ReservationResDto updateReservationStatus(Long productId, Long reservationId, String status) {
        User user = userService.getAuthenticatedUser();
        Reservation reservation = reservationRepository.findByProductIdAndUserIdAndId(productId, user.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 수락 또는 거절 상태입니다.");
        }

        reservation.updateStatus(ReservationStatus.valueOf(status));
        Reservation updatedReservation = reservationRepository.save(reservation);

        return new ReservationResDto(
                updatedReservation.getId(),
                updatedReservation.getUser().getId(),
                updatedReservation.getProduct().getId(),
                updatedReservation.getPart().getId(),
                updatedReservation.getReservationDate(),
                updatedReservation.getNumber(),
                updatedReservation.getTotalPrice(),
                updatedReservation.getStatus(),
                updatedReservation.getCreatedAt(),
                updatedReservation.getUpdatedAt()
        );
    }
}
