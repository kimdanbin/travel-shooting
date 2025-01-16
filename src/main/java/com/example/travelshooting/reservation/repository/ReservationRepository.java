package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    default Reservation findReservationById(Long reservationId) {
        return findById(reservationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + reservationId + "에 해당하는 레저/티켓 예약을 찾을 수 없습니다."));
    }

    @Query("SELECT COALESCE(SUM(r.number), 0) FROM Reservation r WHERE r.part.id = :partId")
    int findTotalNumberByPartId(@Param("partId") Long partId);

    List<Reservation> findAllByUserIdAndProductId(Long userId, Long productId);

    Reservation findReservationByUserIdAndProductIdAndId(Long userId, Long productId, Long reservationId);

    @Query("SELECT r " +
            "FROM Reservation r " +
            "INNER JOIN r.product p " +
            "INNER JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId")
    List<Reservation> findAllByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);

    @Query("SELECT r " +
            "FROM Reservation r " +
            "INNER JOIN r.product p " +
            "INNER JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId AND r.id = :reservationId")
    Reservation findReservationByProductIdAndUserIdAndId(@Param("productId") Long productId, @Param("userId") Long userId, @Param("reservationId") Long reservationId);

    Reservation findReservationByProductIdAndId(Long productId, Long reservationId);
}
