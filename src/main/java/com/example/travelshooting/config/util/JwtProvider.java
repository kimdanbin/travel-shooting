package com.example.travelshooting.config.util;

import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

  /**
   * JWT 시크릿 키.
   */
  @Value("${jwt.secret}")
  private String secret;

  /**
   * 토큰 만료시간(밀리초).
   */
  @Getter
  @Value("${jwt.expiry-millis}")
  private long expiryMillis;

  /**
   * 리프레시 토큰 만료시간(밀리초).
   */
//  @Getter
//  @Value("${jwt.refresh-expiry-millis}")
//  private long refreshExpiryMillis;

  private final UserRepository userRepository;

  public String generateToken(Authentication authentication) {
    String username = authentication.getName();

    return this.generateTokenBy(username);
  }
  // Refresh token
//  public TokenDto generateToken(Authentication authentication) throws EntityNotFoundException {
//
//    String email = authentication.getName();
//
//    String accessToken = this.generateTokenBy(email);
//    String refreshToken = this.generateRefreshToken(email);
//
//    return new TokenDto(accessToken, refreshToken);
//  }

//  public TokenDto generateToken(String email) throws EntityNotFoundException {
//
//    String accessToken = this.generateTokenBy(email);
//    String refreshToken = this.generateRefreshToken(email);
//
//    return new TokenDto(accessToken, refreshToken);
//  }

  public String getUsername(String token) {
    Claims claims = this.getClaims(token);

    return claims.getSubject();
  }

  public boolean validToken(String token) throws JwtException {
    try {
      return !this.tokenExpired(token);
    } catch (MalformedJwtException e) {
      log.error("잘못된 JWT 토큰입니다.: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT 토큰이 만료되었습니다.: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰입니다.: {}", e.getMessage());
    }

    return false;
  }

  private String generateTokenBy(String email) {
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 이메일 입니다.");
    }

    Date currentDate = new Date();
    // 현재 시간을 밀리초로 환산 + 만료시간
    Date expireDate = new Date(currentDate.getTime() + expiryMillis);
    // JWT 생성 코드, Jwts는 io.jsonwebtoken 라이브러리에서 제공하는 JWT 생성 도구
    return Jwts.builder()
        .subject(email) // 주체 -> 사용자 이메일
        .issuedAt(currentDate) // 발급시간
        .expiration(expireDate) // 만료시간
        .claim("role", user.get().getRole()) // 사용자의 역할(Role)을 role이라는 클레임에 추가, 클레임은 JWT의 Payload에 포함되며, 인증 및 인가 과정에서 사용
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256) // 비밀 키 생성 , 문자열을 바이트 배열로 변환
        .compact(); // 토큰 생성
  }

  private Claims getClaims(String token) {
    if (!StringUtils.hasText(token)) { //Spring의 StringUtils 클래스의 메서드로, 입력받은 문자열이 비어있거나 공백으로만 구성되어 있는지 확인
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰이 비어 있습니다.");
    }

    return Jwts.parser()  // 토큰을 파싱(분석)하고 검증
        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))) // 서명(Signature)을 검증하는 데 사용할 비밀 키(secret key)를 설정
        .build() // 설정된 검증 규칙(비밀 키)을 적용한 JWT 파서 객체를 생성
        .parseSignedClaims(token) // 입력받은 JWT 토큰을 파싱하여 서명(Signature)을 검증하고, 데이터(Payload)를 추출
        .getPayload(); // 서명이 유효한 경우, JWT의 Payload 부분에 저장된 데이터를 반환
  }

  private boolean tokenExpired(String token) {
    final Date expiration = this.getExpirationDateFromToken(token);

    return expiration.before(new Date());
  }
    // 리프레시 토큰 생성
//  public String generateRefreshToken(String email) {
//    Date currentDate = new Date();
//    Date expireDate = new Date(currentDate.getTime() + this.refreshExpiryMillis);
//
//    return Jwts.builder()
//        .subject(email)
//        .issuedAt(currentDate)
//        .expiration(expireDate)
//        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
//        .compact();
//  }

//  // 리프레시 토큰 검증
//  public boolean validRefreshToken(String token) {
//    try {
//      Claims claims = this.getClaims(token);
//      return claims.getExpiration().after(new Date());
//    } catch (JwtException e) {
//      log.error("Invalid refresh token: {}", e.getMessage());
//      return false;
//    }
//  }

  private Date getExpirationDateFromToken(String token) {
    return this.resolveClaims(token, Claims::getExpiration);
  }

  private <T> T resolveClaims(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = this.getClaims(token);

    return claimsResolver.apply(claims);
  }
}

