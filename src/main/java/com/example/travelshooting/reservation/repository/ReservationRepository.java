package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserIdAndProductId(Long userId, Long productId);

    Reservation findByUserIdAndProductIdAndId(Long userId, Long productId, Long reservationId);

    @Query("SELECT r " +
            "FROM Reservation r " +
            "JOIN r.product p " +
            "JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId")
    List<Reservation> findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);

    @Query("SELECT r " +
            "FROM Reservation r " +
            "JOIN r.product p " +
            "JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId AND r.id = :reservationId")
    Reservation findByProductIdAndUserIdAndId(@Param("productId") Long productId, @Param("userId") Long userId, @Param("reservationId") Long reservationId);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = :status")
    Reservation updateReservationStatus(@Param("status")ReservationStatus status, @Param("reservationId") Long reservationId);
}
