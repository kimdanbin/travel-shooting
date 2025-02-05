package com.example.travelshooting.poster.repository;

import com.example.travelshooting.poster.entity.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosterRepository extends JpaRepository<Poster,Long>, PosterCustomRepository {

    @Query("SELECT p FROM Poster p WHERE p.id = :id")
    Optional<Poster> findByIdIncludeDeleted(@Param("id") Long id);
}
