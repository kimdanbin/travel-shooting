package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    default Reservation findReservationById(Long reservationId) {
        return findById(reservationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + reservationId + "에 해당하는 레저/티켓 예약을 찾을 수 없습니다."));
    }

    @Query("SELECT COALESCE(SUM(r.headCount), 0) FROM Reservation r WHERE r.part.id = :partId AND r.reservationDate = :reservationDate")
    Integer findTotalHeadCountByPartIdAndReservationDate(@Param("partId") Long partId, @Param("reservationDate") LocalDate reservationDate);

    Optional<Reservation> findReservationByUserIdAndReservationDate(Long userId, LocalDate reservationDate);

    @EntityGraph(attributePaths = {"part", "part.product", "user"})
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.part.product.id = :productId AND r.id = :reservationId")
    Reservation findReservationByUserIdAndProductIdAndId(@Param("userId") Long userId, @Param("productId") Long productId, @Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r INNER JOIN Payment p ON r.id = p.reservation.id WHERE p.id = :paymentId AND p.userId = :userId")
    Reservation findReservationByPaymentIdAndUserId(@Param("paymentId") Long paymentId, @Param("userId") Long userId);

    List<Reservation> findAllByStatus(ReservationStatus reservationStatus);
}