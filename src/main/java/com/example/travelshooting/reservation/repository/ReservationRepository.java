package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    default Reservation findReservationById(Long reservationId) {
        return findById(reservationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + reservationId + "에 해당하는 레저/티켓 예약을 찾을 수 없습니다."));
    }

    @Query("SELECT COALESCE(SUM(r.headCount), 0) FROM Reservation r WHERE r.part.id = :partId AND r.reservationDate = :reservationDate")
    Integer findTotalHeadCountByPartIdAndReservationDate(@Param("partId") Long partId, @Param("reservationDate")LocalDate reservationDate);

    boolean existsByUserIdAndReservationDate(Long userId, LocalDate reservationDate);

    @EntityGraph(attributePaths = {"part", "part.product", "user"})
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.part.product.id = :productId")
    Page<Reservation> findAllByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"part", "part.product", "user"})
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.part.product.id = :productId AND r.id = :reservationId")
    Reservation findReservationByUserIdAndProductIdAndId(@Param("userId") Long userId, @Param("productId") Long productId, @Param("reservationId") Long reservationId);

    @Query("SELECT r " +
            "FROM Reservation r " +
            "INNER JOIN r.part pa " +
            "INNER JOIN pa.product p " +
            "INNER JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId")
    Page<Reservation> findAllByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r " +
            "FROM Reservation r " +
            "INNER JOIN r.part pa " +
            "INNER JOIN pa.product p " +
            "INNER JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId AND r.id = :reservationId")
    Reservation findReservationByProductIdAndUserIdAndId(@Param("productId") Long productId, @Param("userId") Long userId, @Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r WHERE r.updatedAt <= :expirationTime AND r.status = 'APPROVED' AND r.isDeleted = false")
    List<Reservation> findExpiredReservations(@Param("expirationTime") LocalDateTime expirationTime);

    @Query("SELECT r FROM Reservation r INNER JOIN Payment p ON r.id = p.reservation.id WHERE p.id = :paymentId AND p.userId = :userId")
    Reservation findReservationByPaymentIdAndUserId(@Param("paymentId") Long paymentId, @Param("userId") Long userId);
}