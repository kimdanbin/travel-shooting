package com.example.travelshooting.file.repository;

import com.example.travelshooting.file.entity.LeisureFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeisureFileRepository extends JpaRepository<LeisureFile, Long> {
}
