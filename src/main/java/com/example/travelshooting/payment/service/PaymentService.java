package com.example.travelshooting.payment.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.payment.Payment;
import com.example.travelshooting.payment.dto.PaymentAproveResDto;
import com.example.travelshooting.payment.dto.PaymentReadyResDto;
import com.example.travelshooting.payment.repository.PaymentRepository;
import com.example.travelshooting.product.Product;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final ReservationService reservationService;
    private final UserService userService;
    private final ProductService productService;

    @Value("${kakao.api.pay.key}")
    private String apiKey;

    @Value("${kakao.api.pay.ready.url}")
    private String KAKAO_PAY_READY_URL;

    @Value("${kakao.api.pay.approve.url}")
    private String KAKAO_PAY_APPROVE_URL;

    // 카카오페이 결제창 연결
    public PaymentReadyResDto payReady(Long productId, Long reservationId) {
        Reservation reservation = reservationService.findByProductIdAndId(productId, reservationId);
        User user = userService.getAuthenticatedUser();
        Product product = productService.findProductById(productId);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", Const.KAKAO_PAY_TEST_CID);
        body.add("partner_order_id", reservation.getId().toString());
        body.add("partner_user_id", user.getId().toString());
        body.add("item_name", product.getName());
        body.add("quantity", String.valueOf(reservation.getNumber()));
        body.add("total_amount", String.valueOf(reservation.getTotalPrice()));
        body.add("tax_free_amount", "0");
        body.add("approval_url", Const.KAKAO_PAY_APPROVE_URL);
        body.add("cancel_url", Const.KAKAO_PAY_CANCEL_URL);
        body.add("fail_url", Const.KAKAO_PAY_FAIL_URL);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_PAY_READY_URL, HttpMethod.POST, request, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String tid = root.path("tid").asText(); // 결제 고유 번호
                String redirectUrl = root.path(Const.KAKAO_PAY_NEXT_URL).asText();  // 결제 페이지 URL

                // 결제 정보 저장
                savePayment(reservation, tid, reservation.getTotalPrice());

                return new PaymentReadyResDto(redirectUrl);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 준비 응답 처리 중 오류가 발생했습니다.");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 준비 요청 실패.");
        }
    }

    public Payment savePayment(Reservation reservation, String tid, int totalPrice) {
        Payment payment = new Payment(reservation, tid, totalPrice);

        return paymentRepository.save(payment);
    }

    // 최종적으로 결제 완료 처리를 하는 단계
    public PaymentAproveResDto payApprove(Long productId, Long reservationId, Long paymentId, String pgToken) {
        Reservation reservation = reservationService.findByProductIdAndId(productId, reservationId);
        User user = userService.getAuthenticatedUser();
        Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", Const.KAKAO_PAY_TEST_CID);
        body.add("tid", payment.getTid());
        body.add("partner_order_id", reservation.getId().toString());
        body.add("partner_user_id", user.getId().toString());
        body.add("pg_token", pgToken);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

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
        headers.set("Authorization", "KakaoAK " + apiKey);
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }
}
