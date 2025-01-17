package com.example.travelshooting.part.repository;

import com.example.travelshooting.part.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    default Part findPartByIdOrElseThrow(Long partId) {
        return findById(partId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + partId + "에 해당하는 레저/티켓 일정을 찾을 수 없습니다."));
    }

    Part findPartByProductId(Long productId);

    List<Part> findPartsByProductId(Long productId);

    boolean existsByOpenAtAndCloseAt(LocalTime openAt, LocalTime closeAt);

}
