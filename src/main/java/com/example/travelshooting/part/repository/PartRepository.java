package com.example.travelshooting.part.repository;

import com.example.travelshooting.part.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@Repository
public interface PartRepository extends JpaRepository<Part, Long>, PartCustomRepository {

    default Part findPartById(Long partId) {
        return findById(partId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + partId + "에 해당하는 레저/티켓 일정을 찾을 수 없습니다."));
    }

    boolean existsByProductIdAndOpenAtAndCloseAt(Long productId, LocalTime openAt, LocalTime closeAt);

    @Query("SELECT p FROM Part p JOIN FETCH p.reservations r WHERE p.id = :partId ")
    Part findReservationById(@Param("partId") Long partId);

    Part findPartByIdAndProductId(Long partId, Long productId);
}
