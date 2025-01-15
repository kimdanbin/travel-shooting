package com.example.travelshooting.like.repository;

import com.example.travelshooting.like.entity.LikePoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePosterRepository extends JpaRepository<LikePoster, Long> {

    boolean existsByUserIdAndPosterId(Long userId, Long likePosterId);
}
