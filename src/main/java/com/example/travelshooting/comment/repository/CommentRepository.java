package com.example.travelshooting.comment.repository;

import com.example.travelshooting.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPosterId(Long posterId);

    @Query("SELECT p FROM Comment p WHERE p.id = :id")
    Optional<Comment> findByIdIncludeDeleted(@Param("id") Long id);
}
