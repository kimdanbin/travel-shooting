package com.example.travelshooting.comment.repository;

import com.example.travelshooting.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPosterId(Long posterId);


}
