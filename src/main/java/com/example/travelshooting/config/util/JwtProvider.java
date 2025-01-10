package com.example.travelshooting.config.util;

import com.example.travelshooting.user.User;
import com.example.travelshooting.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

  private final UserRepository userRepository;

  public String generateToken(Authentication authentication) throws EntityNotFoundException {
    String username = authentication.getName();
    return this.generateTokenBy(username);
  }

  public String getUsername(String token) {
    Claims claims = this.getClaims(token);
    return claims.getSubject();
  }

  public boolean validToken(String token) throws JwtException {
    try {
      return !tokenExpired(token);
    } catch (MalformedJwtException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다.");
    } catch (ExpiredJwtException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
    } catch (UnsupportedJwtException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
    }
  }

  private String generateTokenBy(String email) throws EntityNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isEmpty()) {
      throw new EntityNotFoundException("유효하지 않은 이메일 입니다.");
    }

    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + expiryMillis);

    return Jwts.builder()
        .subject(email)
        .issuedAt(currentDate)
        .expiration(expireDate)
        .claim("role", user.get().getRole())
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
        .compact();
  }

  private Claims getClaims(String token) {
    if (!StringUtils.hasText(token)) {
      throw new MalformedJwtException("토큰이 비어 있습니다.");
    }

    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private boolean tokenExpired(String token) {
    final Date expiration = this.getExpirationDateFromToken(token);

    return expiration.before(new Date());
  }

  private Date getExpirationDateFromToken(String token) {
    return this.resolveClaims(token, Claims::getExpiration);
  }

  private <T> T resolveClaims(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = this.getClaims(token);

    return claimsResolver.apply(claims);
  }
}

