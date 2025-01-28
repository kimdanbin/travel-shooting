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
//  @PostMapping("/logout")
//  public ResponseEntity<CommonResDto<String>> logout(HttpServletRequest request,
//                                                     HttpServletResponse response, Authentication authentication)
//      throws UsernameNotFoundException {
//    // 인증 정보가 있다면 로그아웃 처리.
//    if (authentication != null && authentication.isAuthenticated()) {
//      new SecurityContextLogoutHandler().logout(request, response, authentication);
//
//      return ResponseEntity.ok(new CommonResDto<>("로그아웃 성공."));
//    }
//    throw new UsernameNotFoundException("로그인이 먼저 필요합니다.");
//  }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String email) {
        userService.logout(email); // 서비스 레이어 호출
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 비밀번호 확인 후 회원탈퇴
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<String> deleteUser(
//            @PathVariable Long userId,
//            @RequestBody PasswordVrfReqDto requestDto) {
//        userService.deleteUser(userId, requestDto);
//
//        return new ResponseEntity<>("회원 탈퇴 성공", HttpStatus.OK);
//    }
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
//    @PostMapping("/refresh")
//    public ResponseEntity<CommonResDto<TokenDto>> refreshAccessToken(@Valid @RequestBody TokenDto dto) {
//
//        TokenDto result = userService.refresh(dto.getAccessToken(), dto.getRefreshToken());
//
//        return new ResponseEntity<>(new CommonResDto<>("토큰 재발급 완료", result), HttpStatus.CREATED);
//    }

    // 사용자 이미지 추가
    @PostMapping("/{userId}/attachments")
    public ResponseEntity<CommonResDto<UserResDto>> uploadFile(@PathVariable Long userId,
                                                               @RequestParam MultipartFile file) {
        UserResDto user = userService.uploadFile(userId, file);

        return new ResponseEntity<>(new CommonResDto<>("파일 업로드 완료", user), HttpStatus.OK);
    }
}
