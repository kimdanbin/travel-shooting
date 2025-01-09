package com.example.travelshooting.part.repository;

import com.example.travelshooting.part.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    Part findPartByProductId(Long productId);

    List<Part> findPartsByProductId(Long productId);

}
