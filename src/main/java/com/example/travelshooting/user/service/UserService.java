package com.example.travelshooting.user.service;

import com.example.travelshooting.config.util.JwtProvider;
import com.example.travelshooting.enums.AuthenticationScheme;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.user.dto.ChangePasswordReqDto;
import com.example.travelshooting.user.dto.JwtAuthResDto;
import com.example.travelshooting.user.dto.PasswordVrfReqDto;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;

  @Transactional
  public UserResDto userSignup(String email, String password, String name) {
    Optional<User> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(password);

    User user = new User(email, encodedPassword, name, UserRole.USER);
    User savedUser = userRepository.save(user);

    return new UserResDto(
        savedUser.getId(),
        savedUser.getEmail(),
        savedUser.getName(),
        savedUser.getRole()
    );
  }

  public JwtAuthResDto login(String email, String password) {    // 사용자 확인.
    // 사용자 확인.
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
    }

    validatePassword(password, user.get().getPassword());

    // 사용자 인증 후 인증 객체를 저장
    Authentication authentication = this.authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 토큰 생성
    String accessToken = jwtProvider.generateToken(authentication);

    return new JwtAuthResDto(AuthenticationScheme.BEARER.getName(), accessToken);
  }

  //비밀번호 변경
  public void changePassword(Long userId, ChangePasswordReqDto passwordRequestDto) {
    Optional<User> userOptional = userRepository.findById(userId);

    User user = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

    // 기존 비밀번호 검증
    validatePassword(passwordRequestDto.getOldPassword(), user.getPassword());

    // 새 비밀번호와 기존 비밀번호 동일성 확인
    if (passwordEncoder.matches(passwordRequestDto.getNewPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 동일할 수 없습니다.");
    }

    // 비밀번호 변경
    user.updatePassword(passwordEncoder.encode(passwordRequestDto.getNewPassword()));
    userRepository.save(user);
  }
  // 회원 탈퇴
  public void deleteUser(Long userId, PasswordVrfReqDto requestDto) {
    // 사용자 조회
    User user = userRepository.findUserById(userId);

    // 비밀번호 확인
    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
    }

    // 사용자 삭제
    userRepository.delete(user);
  }

  private void validatePassword(String rawPassword, String encodedPassword) throws IllegalArgumentException {
    boolean notValid = !this.passwordEncoder.matches(rawPassword, encodedPassword);

    if (notValid) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다.");
    }
  }

  public User findUserById(Long userId) {
    return userRepository.findUserById(userId);
  }

  public User findAuthenticatedUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String authEmail = auth.getName();
    Optional<User> user = userRepository.findByEmail(authEmail);

    return user.get();
  }
}
