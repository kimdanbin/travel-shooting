package com.example.travelshooting.payment.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.payment.dto.PaymentCancelResDto;
import com.example.travelshooting.payment.dto.PaymentCompletedResDto;
import com.example.travelshooting.payment.dto.PaymentReadyResDto;
import com.example.travelshooting.payment.dto.PaymentResDto;
import com.example.travelshooting.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 준비
    @PostMapping("/products/{productId}/reservations/{reservationId}/payments/ready")
    public ResponseEntity<CommonResDto<PaymentReadyResDto>> payReady(@PathVariable Long productId,
                                                                     @PathVariable Long reservationId) {
        PaymentReadyResDto payReady = paymentService.payReady(productId, reservationId);

        return new ResponseEntity<>(new CommonResDto<>("결제 준비 완료", payReady), HttpStatus.OK);
    }

    // 결제 승인
    @GetMapping("/products/{productId}/reservations/{reservationId}/payments/completed")
    public ResponseEntity<CommonResDto<PaymentCompletedResDto>> payCompleted(@PathVariable Long productId,
                                                                             @PathVariable Long reservationId,
                                                                             @RequestParam("pg_token") String pgToken) {
        PaymentCompletedResDto payCompleted = paymentService.payCompleted(productId, reservationId, pgToken);

        return new ResponseEntity<>(new CommonResDto<>("결제 승인 완료", payCompleted), HttpStatus.OK);
    }

    // 결제 취소
    @PostMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<CommonResDto<PaymentCancelResDto>> payCancel(@PathVariable Long paymentId) {
        PaymentCancelResDto payCancel = paymentService.payCancel(paymentId);

        return new ResponseEntity<>(new CommonResDto<>("결제 취소 완료", payCancel), HttpStatus.OK);
    }

    // 결제 실패
    @GetMapping("/payments/fail")
    public ResponseEntity<String> payFail() {
        return new ResponseEntity<>("결제 실패", HttpStatus.BAD_REQUEST);
    }

    // 사용자 결제 내역 전체 조회
    @GetMapping("/payments")
    public ResponseEntity<CommonListResDto<PaymentResDto>> findPayments(Pageable pageable) {
        Page<PaymentResDto> payments = paymentService.findPayments(pageable);
        List<PaymentResDto> content = payments.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("결제 내역 전체 조회 완료", content), HttpStatus.OK);
    }

    // 사용자 결제 내역 단 건 조회
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<CommonResDto<PaymentResDto>> findPaymentByIdAndUserId(@PathVariable Long paymentId) {
        PaymentResDto payment = paymentService.findPaymentByIdAndUserId(paymentId);

        return new ResponseEntity<>(new CommonResDto<>("결제 내역 단 건 조회 완료", payment), HttpStatus.OK);
    }
}