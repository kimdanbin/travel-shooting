package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserIdAndProductId(Long userId, Long productId);

    Reservation findByUserIdAndProductIdAndId(Long userId, Long productId, Long reservationId);

    @Query("SELECT new com.example.travelshooting.reservation.dto.ReservationResDto(r.id, r.user.id, r.product.id, r.part.id, r.reservationDate, r.number, r.totalPrice, r.status, r.createdAt, r.updatedAt) " +
            "FROM Reservation r " +
            "JOIN r.product p " +
            "JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId")
    List<ReservationResDto> findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);
}
