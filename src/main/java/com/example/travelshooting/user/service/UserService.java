package com.example.travelshooting.user.service;

import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.enums.AuthenticationScheme;
import com.example.travelshooting.config.util.JwtProvider;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.dto.JwtAuthResDto;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
            throw new EntityNotFoundException("유효하지 않는 이메일입니다.");
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

    private void validatePassword(String rawPassword, String encodedPassword) throws IllegalArgumentException {
        boolean notValid = !this.passwordEncoder.matches(rawPassword, encodedPassword);

        if (notValid) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND , "아이디 " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));
    }
}
