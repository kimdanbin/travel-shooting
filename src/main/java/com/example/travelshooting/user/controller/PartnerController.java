package com.example.travelshooting.user.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.user.dto.UserReqDto;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.service.PartnerService;
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
@RequestMapping("/partners")
public class PartnerController {

  private final PartnerService partnerService;

  @PostMapping
  public ResponseEntity<CommonResDto<UserResDto>> partnerSignup(@Valid @RequestBody UserReqDto userReqDto) {
    UserResDto partnerSignup = partnerService.partnerSignup(userReqDto.getEmail(), userReqDto.getPassword(), userReqDto.getName());

    return new ResponseEntity<>(new CommonResDto<>("협력 업체 회원가입 완료", partnerSignup), HttpStatus.CREATED);
  }
}
