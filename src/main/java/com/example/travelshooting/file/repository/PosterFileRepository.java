package com.example.travelshooting.file.repository;

import com.example.travelshooting.file.entity.PosterFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterFileRepository extends JpaRepository<PosterFile, Long> {
}
