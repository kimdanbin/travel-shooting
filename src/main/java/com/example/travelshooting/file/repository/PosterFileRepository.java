package com.example.travelshooting.file.repository;

import com.example.travelshooting.file.entity.PosterFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosterFileRepository extends JpaRepository<PosterFile, Long> {

    @Query(value = "SELECT * FROM poster_file " +
            "WHERE file_type LIKE 'video/%' " +
            "ORDER BY created_at DESC " +
            "LIMIT 5", nativeQuery = true)
    List<PosterFile> getNewFiveShorts();
}
