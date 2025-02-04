package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationCustomRepository {

    Page<ReservationResDto> findAllByUserIdAndProductId(Long productId, User authenticatedUser, Pageable pageable);

    ReservationResDto findReservationByProductIdAndId(Long productId, Long reservationId, User authenticatedUser);
}
