package com.example.travelshooting.user.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.user.dto.*;
import com.example.travelshooting.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 권한으로 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResDto<UserResDto>> userSignup(
            @RequestPart(required = false) MultipartFile file,
            @Valid @RequestPart UserReqDto userReqDto
    ) {
        UserResDto userSignup = userService.userSignup(userReqDto.getEmail(), userReqDto.getPassword(), userReqDto.getName(), file);

        return new ResponseEntity<>(new CommonResDto<>("사용자 회원가입 완료", userSignup), HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResDto<JwtAuthResDto>> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        JwtAuthResDto login = userService.login(loginReqDto.getEmail(), loginReqDto.getPassword());

        return new ResponseEntity<>(new CommonResDto<>("로그인 완료", login), HttpStatus.OK);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        userService.logout(accessToken); // 서비스 레이어 호출

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 비밀번호 확인
    @PostMapping("/{userId}/verify-password")
    public ResponseEntity<String> verifyPassword(
            @PathVariable Long userId,
            @RequestBody PasswordVrfReqDto reqDto) {
        boolean isVerified = userService.verifyPassword(userId, reqDto);

        if (isVerified) {
            return ResponseEntity.ok("비밀번호 확인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비밀번호가 일치하지 않습니다.");
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return new ResponseEntity<>("회원 탈퇴 성공", HttpStatus.OK);
    }


    // 비밀번호 변경
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordReqDto passwordRequestDto) {

        userService.changePassword(userId, passwordRequestDto);

        return new ResponseEntity<>("비밀번호 변경 완료", HttpStatus.OK);
    }

    // Access token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResDto> refreshAccessToken(@Valid @RequestBody TokenDto tokenDto) {
        JwtAuthResDto response = userService.refreshAccessToken(tokenDto.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 사용자 이미지 추가
    @PostMapping("/{userId}/attachments")
    public ResponseEntity<CommonResDto<UserResDto>> uploadFile(@PathVariable Long userId,
                                                               @RequestParam MultipartFile file) {
        UserResDto user = userService.uploadFile(userId, file);

        return new ResponseEntity<>(new CommonResDto<>("파일 업로드 완료", user), HttpStatus.OK);
    }
}
