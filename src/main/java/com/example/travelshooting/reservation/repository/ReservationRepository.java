package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserIdAndProductId(Long userId, Long productId);

    Reservation findByUserIdAndProductIdAndId(Long userId, Long productId, Long reservationId);
}
