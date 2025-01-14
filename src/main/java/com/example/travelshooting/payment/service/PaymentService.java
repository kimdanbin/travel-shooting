package com.example.travelshooting.payment.service;

import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.payment.Payment;
import com.example.travelshooting.payment.dto.PaymentAproveResDto;
import com.example.travelshooting.payment.dto.PaymentReadyResDto;
import com.example.travelshooting.payment.repository.PaymentRepository;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.service.ReservationService;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final ReservationService reservationService;
    private final UserService userService;
    private final ProductService productService;

    @Value("${kakao.api.pay.key}")
    private String SecretKey;

    @Value("${kakao.api.pay.ready.url}")
    private String KAKAO_PAY_READY_URL;

    @Value("${kakao.api.pay.approve.url}")
    private String KAKAO_PAY_APPROVE_URL;

    // 카카오페이 결제창 연결
    public PaymentReadyResDto payReady(Long productId, Long reservationId) {
        Reservation reservation = reservationService.findByProductIdAndId(productId, reservationId);
        User user = userService.getAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Payment payment = paymentRepository.findByReservationId(reservation.getId());

        if (!reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 승인이 먼저 되어야 합니다.");
        }

        if (payment != null && payment.getStatus().equals(PaymentStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 결제된 예약입니다.");
        }

        String approvalUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
                .path("/products/{productId}/reservations/{reservationId}/payment/approve")
                .buildAndExpand(productId, reservationId)
                .toUriString();

        Map<String, String> body = new HashMap<>();
        body.put("cid", "TC0ONETIME");
        body.put("partner_order_id", reservation.getId().toString());
        body.put("partner_user_id", user.getId().toString());
        body.put("item_name", product.getName());
        body.put("quantity", String.valueOf(reservation.getNumber()));
        body.put("total_amount", String.valueOf(reservation.getTotalPrice()));
        body.put("tax_free_amount", "0");
        body.put("approval_url", approvalUrl);
        body.put("cancel_url", "http://localhost:8080/payment/cancel");
        body.put("fail_url", "http://localhost:8080/payment/fail");

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_PAY_READY_URL, HttpMethod.POST, request, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String tid = root.path("tid").asText(); // 결제 고유 번호
                String redirectUrl = root.path("next_redirect_pc_url").asText();  // 결제 페이지 URL

                // 결제 정보 저장
                savePayment(reservation, tid, user.getId(), reservation.getTotalPrice());

                return new PaymentReadyResDto(redirectUrl);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 준비 응답 처리 중 오류가 발생했습니다.");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 준비 요청 실패.");
        }
    }

    public Payment savePayment(Reservation reservation, String tid, Long userId, int totalPrice) {
        Payment payment = new Payment(reservation, tid, userId, totalPrice);

        return paymentRepository.save(payment);
    }

    // 최종적으로 결제 완료 처리를 하는 단계
    public PaymentAproveResDto payApprove(Long productId, Long reservationId, String pgToken) {
        Reservation reservation = reservationService.findByProductIdAndId(productId, reservationId);
        Payment payment = paymentRepository.findByReservationId(reservationId);

        Map<String, String> body = new HashMap<>();
        body.put("cid", "TC0ONETIME");
        body.put("tid", payment.getTid());
        body.put("partner_order_id", reservation.getId().toString());
        body.put("partner_user_id", payment.getUserId().toString());
        body.put("pg_token", pgToken);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_PAY_APPROVE_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root = objectMapper.readTree(response.getBody());

                String aid = root.path("aid").asText();
                String tid = root.path("tid").asText();
                String partnerOrderId = root.path("partner_order_id").asText();
                String partnerUserId = root.path("partner_user_id").asText();
                String itemName = root.path("item_name").asText();
                int quantity = root.path("quantity").asInt();
                int total = root.path("total").asInt();

                PaymentAproveResDto paymentAproveResDto = new PaymentAproveResDto(
                        aid,
                        tid,
                        partnerOrderId,
                        partnerUserId,
                        itemName,
                        quantity,
                        total
                );

                payment.updatePayStatus(PaymentStatus.APPROVED);
                paymentRepository.save(payment);

                return paymentAproveResDto;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 승인 응답 처리 중 오류가 발생했습니다.");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 승인 요청 실패.");
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SecretKey);
        headers.set("Content-type", "application/json");

        return headers;
    }
}