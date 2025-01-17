package com.example.travelshooting.payment.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.payment.dto.PaymentAproveResDto;
import com.example.travelshooting.payment.dto.PaymentReadyResDto;
import com.example.travelshooting.payment.entity.Payment;
import com.example.travelshooting.payment.repository.PaymentRepository;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.service.ReservationService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
    private String secretKey;

    @Value("${kakao.api.pay.ready.url}")
    private String kakaoPayReadyUrl;

    @Value("${kakao.api.pay.approve.url}")
    private String kakaoPayApproveUrl;

    @Value("${kakao.api.pay.next.url}")
    private String kakaoPayNextUrl;

    @Value("${kakao.api.pay.cancel.url}")
    private String kakaoPayCancelUrl;

    @Value("${kakao.api.pay.fail.url}")
    private String kakaoPayFailUrl;

    @Value("${kakao.api.pay.completed.url}")
    private String kakaoPayCompletedUrl;

    // 카카오페이 결제창 연결
    public PaymentReadyResDto payReady(Long productId, Long reservationId) {
        Reservation reservation = reservationService.findReservationByProductIdAndId(productId, reservationId);
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Payment payment = paymentRepository.findByReservationId(reservation.getId());

        if (!reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 승인이 먼저 되어야 합니다.");
        }

        if (payment != null && payment.getStatus().equals(PaymentStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 결제된 예약입니다.");
        }

        String approvalUrl = String.format(
                kakaoPayCompletedUrl,
                product.getId(),
                reservation.getId()
        );

        Map<String, String> body = new HashMap<>();
        body.put("cid", Const.KAKAO_PAY_TEST_CID);
        body.put("partner_order_id", reservation.getId().toString());
        body.put("partner_user_id", user.getId().toString());
        body.put("item_name", product.getName());
        body.put("quantity", String.valueOf(reservation.getNumber()));
        body.put("total_amount", String.valueOf(reservation.getTotalPrice()));
        body.put("tax_free_amount", "0");
        body.put("approval_url", approvalUrl);
        body.put("cancel_url", kakaoPayCancelUrl);
        body.put("fail_url", kakaoPayFailUrl);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoPayReadyUrl, HttpMethod.POST, request, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String tid = root.path("tid").asText();
                String redirectUrl = root.path(kakaoPayNextUrl).asText();

                // 결제 정보 저장
                savePayment(reservation, tid, user.getId(), reservation.getTotalPrice());

                return new PaymentReadyResDto(redirectUrl);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 준비 응답 처리 중 오류 발생");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 준비 요청 실패");
        }
    }

    public Payment savePayment(Reservation reservation, String tid, Long partnerUserId, Integer totalPrice) {
        Payment payment = new Payment(reservation, tid, partnerUserId, totalPrice);

        return paymentRepository.save(payment);
    }

    // 최종적으로 결제 완료 처리를 하는 단계
    public PaymentAproveResDto payApprove(Long productId, Long reservationId, String pgToken) {
        Reservation reservation = reservationService.findReservationByProductIdAndId(productId, reservationId);
        Payment payment = paymentRepository.findByReservationId(reservationId);

        Map<String, String> body = new HashMap<>();
        body.put("cid", Const.KAKAO_PAY_TEST_CID);
        body.put("tid", payment.getTid());
        body.put("partner_order_id", reservation.getId().toString());
        body.put("partner_user_id", payment.getPartnerUserId().toString());
        body.put("pg_token", pgToken);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoPayApproveUrl,
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
                String paymentType = root.path("payment_method_type").asText();
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

                payment.updatePayment(PaymentStatus.APPROVED, paymentType);
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
        headers.set(HttpHeaders.AUTHORIZATION, Const.KAKAO_PAY_HEADER + " " + secretKey);
        headers.set("Content-type", "application/json");

        return headers;
    }

    public Payment findPaymentById(Long paymentId) {
        return paymentRepository.findPaymentById(paymentId);
    }
}