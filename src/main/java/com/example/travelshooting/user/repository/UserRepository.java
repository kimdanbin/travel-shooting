package com.example.travelshooting.user.repository;

import com.example.travelshooting.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  default User findUserById(Long userId) {
    return this.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));
  }
}
