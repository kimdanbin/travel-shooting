package com.example.travelshooting.reservation.service;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ReservationResDto> findByProductIdAndUserId(Long productId) {
        User partner = userService.getUserById(4L); // 임시 user, 이후 수정 예정
        List<Reservation> reservations = reservationRepository.findByProductIdAndUserId(productId, partner.getId());

        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("아이디 " + productId + "에 해당하는 예약 내역이 없습니다.");
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
        User user = userService.getUserById(4L); // 임시 user, 이후 수정 예정
        Reservation reservation = reservationRepository.findByProductIdAndUserIdAndId(productId, user.getId(), reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("아이디 " + productId + "에 해당하는 예약 내역이 없습니다.");
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
        User user = userService.getUserById(4L); // 임시 user, 이후 수정 예정
        Reservation reservation = reservationRepository.findByProductIdAndUserIdAndId(productId, user.getId(), reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("아이디 " + productId + "에 해당하는 예약 내역이 없습니다.");
        }

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new IllegalArgumentException("이미 수락 또는 거절 상태입니다.");
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
