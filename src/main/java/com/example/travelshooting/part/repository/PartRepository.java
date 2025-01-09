package com.example.travelshooting.part.repository;

import com.example.travelshooting.part.Part;
import com.example.travelshooting.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    default Part findByIdOrElseThrow(Long partId) {
        return findById(partId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "레저/티켓 일정이 존재하지 않습니다."));
    }

    Part findPartByProductId(Long productId);

    List<Part> findPartsByProductId(Long productId);

}
