package com.example.travelshooting.config.exception;

import com.example.travelshooting.common.CommonResDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;

/**
 * 전역에서 예외를 처리하는 핸들러.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validation 예외 처리.
     *
     * @param e HandlerMethodValidationException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<String>>}
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<CommonResDto<String>> handleMethodValidationExceptions(
            HandlerMethodValidationException e) {
        String message = e.getParameterValidationResults().getFirst().getResolvableErrors().getFirst()
                .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResDto<>(message));
    }

    /**
     * Validation 예외 처리.
     *
     * @param e MethodArgumentNotValidException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<String>>}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResDto<String>> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResDto<>(message));
    }

    /**
     * Security와 관련된 AuthenticationException 예외 처리.
     *
     * @param e AuthenticationException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<Void>>}
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<CommonResDto<Void>> handleAuthException(
            AuthenticationException e) {
        HttpStatus statusCode = e instanceof BadCredentialsException
                ? HttpStatus.FORBIDDEN
                : HttpStatus.UNAUTHORIZED;

        return ResponseEntity
                .status(statusCode)
                .body(new CommonResDto<>(e.getMessage()));
    }

    /**
     * Security와 관련된 AccessDeniedException 예외 처리.
     *
     * @param e AccessDeniedException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<Void>>}
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<CommonResDto<Void>> handleAccessDeniedException(
            AccessDeniedException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResDto<>(e.getMessage()));
    }

    /**
     * Security와 관련된 AuthorizationDeniedException 예외 처리.
     *
     * @param e AuthorizationDeniedException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<Void>>}
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<CommonResDto<Void>> handleAuthorizationDeniedException(
            AuthorizationDeniedException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResDto<>(e.getMessage()));
    }

    /**
     * JWT와 관련된 JwtException 예외 처리.
     *
     * @param e JwtException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<Void>>}
     */
    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<CommonResDto<Void>> handleJwtException(JwtException e) {
        HttpStatus httpStatus = e instanceof ExpiredJwtException
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;

        return ResponseEntity
                .status(httpStatus)
                .body(new CommonResDto<>(e.getMessage()));
    }

    /**
     * ResponseStatusException 예외 처리.
     *
     * @param e ResponseStatusException 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<Void>>}
     */
    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<CommonResDto<Void>> handleResponseStatusExceptions(
            ResponseStatusException e) {

        return ResponseEntity
                .status(e.getStatusCode())
                .body(new CommonResDto<>(e.getMessage()));
    }

    /**
     * 그외의 예외 처리.
     *
     * @param e 예외 인스턴스
     * @return {@code ResponseEntity<CommonResponseBody<Void>>}
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResDto<Void>> handleOtherExceptions(Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonResDto<>(e.getMessage()));
    }

    /**
     * 잘못된 날짜 형식 요청일 경우
     *
     * @param e 예외 인스턴스
     * @return 400 상태 코드와 예외 메시지 반환
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResDto<Void>> handleInvalidDateFormat(
            HttpMessageNotReadableException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResDto<>(e.getMessage()));
    }
}

