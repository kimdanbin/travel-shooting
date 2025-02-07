package com.example.travelshooting.user.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.user.dto.UserReqDto;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 관리자 회원가입
    @PostMapping
    public ResponseEntity<CommonResDto<UserResDto>> adminSignup(
            @RequestPart(required = false) MultipartFile file,
            @Valid @RequestPart UserReqDto userReqDto
    ) {
        UserResDto adminSignup = adminService.adminSignup(userReqDto.getEmail(), userReqDto.getPassword(), userReqDto.getName(), file);

        return new ResponseEntity<>(new CommonResDto<>("관리자 회원가입 완료", adminSignup), HttpStatus.CREATED);
    }
}