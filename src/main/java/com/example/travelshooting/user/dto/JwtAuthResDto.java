package com.example.travelshooting.user.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtAuthResDto implements BaseDtoDataType {

  // access token 인증 방식.
  private final String tokenAuthScheme;

  // access token.
  private final String accessToken;
}
