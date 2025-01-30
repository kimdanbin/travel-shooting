package com.example.travelshooting.user.repository;

import com.example.travelshooting.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  default User findUserById(Long userId) {
    return this.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디: " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));
  }

  @Query("SELECT u FROM User u INNER JOIN u.companies c ON u.id = c.user.id WHERE c.id = :companyId")
  User findUserByCompanyId(@Param("companyId") Long companyId);

  @Query("SELECT u FROM User u INNER JOIN Reservation r ON u.id = r.user.id WHERE r.id = :reservationId")
  User findUserByReservationId(@Param("reservationId") Long reservationId);
}
