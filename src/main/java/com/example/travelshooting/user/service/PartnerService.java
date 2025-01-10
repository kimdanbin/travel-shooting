package com.example.travelshooting.user.service;

import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResDto partnerSignup(String email, String password, String name) {
    Optional<User> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(password);

    User user = new User(email, encodedPassword, name, UserRole.PARTNER);
    User savedUser = userRepository.save(user);

    return new UserResDto(
        savedUser.getId(),
        savedUser.getEmail(),
        savedUser.getName(),
        savedUser.getRole()
    );
  }
}
