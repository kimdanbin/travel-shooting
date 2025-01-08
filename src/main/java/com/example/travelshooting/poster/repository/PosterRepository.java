package com.example.travelshooting.poster.repository;

import com.example.travelshooting.comment.Comment;
import com.example.travelshooting.poster.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface PosterRepository extends JpaRepository<Poster,Long> {
}
