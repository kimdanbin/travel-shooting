package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationCustomRepository {

    Page<ReservationResDto> findAllByUserIdAndProductId(Long productId, User authenticatedUser, Pageable pageable);

    ReservationResDto findReservationByProductIdAndId(Long productId, Long reservationId, Long userId);

    Page<ReservationResDto> findPartnerReservationsByProductIdAndUserId(Long productId, User authenticatedUser, Pageable pageable);

    ReservationResDto findPartnerReservationByProductIdAndId(Long productId, Long reservationId, User authenticatedUser);

    Integer findTotalHeadCountByPartIdAndReservationDate(Long partId, LocalDate reservationDate);

    Reservation updateStatusAndIsDeleted(Long reservationId, ReservationStatus status, boolean isDeleted);

    Reservation findReservationByProductIdAndIdAndUserId(Long productId, Long reservationId, Long userId);
}