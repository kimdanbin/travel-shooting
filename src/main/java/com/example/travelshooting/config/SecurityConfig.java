package com.example.travelshooting.config;

import com.example.travelshooting.common.Const;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;

  @Bean
  RestTemplate restTemplate() {
    try {
      SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
      factory.setConnectTimeout(Const.CONNECT_TIMEOUT);
      factory.setReadTimeout(Const.READ_TIMEOUT);

      return new RestTemplate(factory);
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "RestTemplate 객체 생성에 실패했습니다", ex);
    }
  }


  @Bean
  BCryptPasswordEncoder passwordEncoder() {
    try {
      return new BCryptPasswordEncoder();
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 암호화에 실패하였습니다.", ex);
    }
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    try {
      return config.getAuthenticationManager();
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AuthenticationManager 초기화에 실패하였습니다.", ex);
    }
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    try {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());

      return authProvider;
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication provider 초기화에 실패하였습니다.", ex);
    }
  }
}

