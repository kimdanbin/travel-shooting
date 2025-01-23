package com.example.travelshooting.payment.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.payment.dto.PaymentAproveResDto;
import com.example.travelshooting.payment.dto.PaymentReadyResDto;
import com.example.travelshooting.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CommonResDto<PaymentAproveResDto>> payCompleted(@PathVariable Long productId,
                                                                          @PathVariable Long reservationId,
                                                                          @RequestParam("pg_token") String pgToken) {
        PaymentAproveResDto payApprove = paymentService.payCompleted(productId, reservationId, pgToken);

        return new ResponseEntity<>(new CommonResDto<>("결제 승인 완료", payApprove), HttpStatus.OK);
    }

    // 결제 취소
    @GetMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<String> payCancel() {
        return ResponseEntity.ok("결제 취소");
    }

    // 결제 실패
    @GetMapping("payments/fail")
    public ResponseEntity<String> payFail() {
        return ResponseEntity.ok("결제 실패");
    }
}
