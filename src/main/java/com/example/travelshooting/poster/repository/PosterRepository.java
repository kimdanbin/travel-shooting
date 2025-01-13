package com.example.travelshooting.poster.repository;

import com.example.travelshooting.poster.entity.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterRepository extends JpaRepository<Poster,Long> {
}
