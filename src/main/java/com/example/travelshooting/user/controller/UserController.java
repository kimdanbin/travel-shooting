package com.example.travelshooting.user.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.user.dto.JwtAuthResDto;
import com.example.travelshooting.user.dto.LoginReqDto;
import com.example.travelshooting.user.dto.UserReqDto;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  // 사용자 권한으로 회원가입
  @PostMapping("/signup")
  public ResponseEntity<CommonResDto<UserResDto>> userSignup(@Valid @RequestBody UserReqDto userReqDto) {
    UserResDto userSignup = userService.userSignup(userReqDto.getEmail(), userReqDto.getPassword(), userReqDto.getName());

    return new ResponseEntity<>(new CommonResDto<>("사용자 회원가입 완료", userSignup), HttpStatus.CREATED);
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<CommonResDto<JwtAuthResDto>> login(@Valid @RequestBody LoginReqDto loginReqDto) {
    JwtAuthResDto login = userService.login(loginReqDto.getEmail(), loginReqDto.getPassword());

    return new ResponseEntity<>(new CommonResDto<>("로그인 완료", login), HttpStatus.OK);
  }

  // 비밀번호 확인 후 회원탈퇴


  // 비밀번호 변경
}
