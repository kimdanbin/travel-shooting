package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

    default Reservation findReservationById(Long reservationId) {
        return findById(reservationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + reservationId + "에 해당하는 레저/티켓 예약을 찾을 수 없습니다."));
    }

    boolean existsReservationByUserIdAndReservationDate(Long userId, LocalDate reservationDate);

    @Query("SELECT r FROM Reservation r INNER JOIN Payment p ON r.id = p.reservation.id WHERE p.id = :paymentId AND p.userId = :userId")
    Reservation findReservationByPaymentIdAndUserId(@Param("paymentId") Long paymentId, @Param("userId") Long userId);

    List<Reservation> findAllByStatus(ReservationStatus reservationStatus);
}