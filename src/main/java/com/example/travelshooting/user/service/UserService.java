package com.example.travelshooting.user.service;

import com.example.travelshooting.config.util.JwtProvider;
import com.example.travelshooting.enums.AuthenticationScheme;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.file.service.S3Service;
import com.example.travelshooting.user.dto.ChangePasswordReqDto;
import com.example.travelshooting.user.dto.JwtAuthResDto;
import com.example.travelshooting.user.dto.PasswordVrfReqDto;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public UserResDto userSignup(String email, String password, String name, MultipartFile file) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        String fileUrl = "";

        if (file != null) {
            try {
                fileUrl = s3Service.uploadFile(file);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        User user = new User(email, encodedPassword, name, UserRole.USER, fileUrl);
        User savedUser = userRepository.save(user);

        return new UserResDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole(),
                savedUser.getImageUrl()
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        return new JwtAuthResDto(AuthenticationScheme.BEARER.getName(), accessToken, refreshToken);
    }
    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        // 이메일 가져오기
        String email = authentication.getName();

        // Redis에서 Refresh Token 삭제
        Boolean isDeleted = redisTemplate.delete(email);

        // 삭제 여부 확인
        if (Boolean.FALSE.equals(isDeleted)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 로그아웃 되었거나 유효하지 않은 세션입니다.");
        }

        log.info("User with email '{}' successfully logged out.", email);

        jwtProvider.blacklistToken(accessToken);
        log.info("User '{}' 로그아웃 처리 완료. Access Token 블랙리스트에 추가되었습니다.", email);
    }

    @Transactional
    public JwtAuthResDto refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        String email = jwtProvider.getUsername(refreshToken);

        if (!jwtProvider.validateRefreshToken(email, refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다.");
        }

        // Access Token 갱신
        String newAccessToken = jwtProvider.generateAccessToken(email);

        return new JwtAuthResDto(
            AuthenticationScheme.BEARER.getName(),
            newAccessToken,
            refreshToken
        );
    }

    //비밀번호 변경
    @Transactional
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


    // 비밀번호 비교
    public boolean verifyPassword(Long userId, PasswordVrfReqDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 ID의 사용자를 찾을 수 없습니다."));

        return passwordEncoder.matches(requestDto.getPassword(), user.getPassword());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 ID의 사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    private void validatePassword(String rawPassword, String encodedPassword) throws IllegalArgumentException {
        boolean notValid = !this.passwordEncoder.matches(rawPassword, encodedPassword);

        if (notValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다.");
        }
    }

    @Transactional
    public UserResDto uploadFile(Long userId, MultipartFile file) {
        User user = userRepository.findUserById(userId);

        if (!user.getId().equals(findAuthenticatedUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 이미지만 업로드할 수 있습니다.");
        }

        try {
            String fileUrl = s3Service.uploadFile(file);

            user.updateImage(fileUrl);
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new UserResDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getImageUrl()
        );
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