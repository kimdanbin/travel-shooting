package com.example.travelshooting.payment.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.RefundPolicy;
import com.example.travelshooting.enums.RefundType;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.payment.dto.PaymentCancelResDto;
import com.example.travelshooting.payment.dto.PaymentCompletedResDto;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
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

    @Value("${payment.cancel.url}")
    private String paymentCancelUrl;

    @Value("${payment.fail.url}")
    private String paymentFailUrl;

    @Value("${payment.completed.url}")
    private String paymentCompletedUrl;

    // 카카오페이 결제창 연결
    @Transactional
    public PaymentReadyResDto payReady(Long productId, Long reservationId) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        Reservation reservation = reservationService.findReservationByUserIdAndProductIdAndId(user.getId(), product.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역을 찾을 수 없습니다.");
        }

        Payment payment = paymentRepository.findPaymentByReservationId(reservation.getId());

        if (!reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 승인이 먼저 되어야 합니다.");
        }

        if (payment != null && payment.getStatus().equals(PaymentStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 결제된 예약입니다.");
        }

        // 결제 정보 저장
        Payment savedPayment = savePayment(reservation, user.getId(), reservation.getTotalPrice());

        String approvalUrl = String.format(
                paymentCompletedUrl,
                product.getId(),
                reservation.getId()
        );

        String cancelUrl = String.format(
                paymentCancelUrl,
                savedPayment.getId()
        );

        Map<String, String> body = new HashMap<>();
        body.put("cid", Const.KAKAO_PAY_TEST_CID);
        body.put("partner_order_id", reservation.getId().toString());
        body.put("partner_user_id", user.getId().toString());
        body.put("item_name", product.getName());
        body.put("quantity", String.valueOf(reservation.getHeadCount()));
        body.put("total_amount", String.valueOf(reservation.getTotalPrice()));
        body.put("tax_free_amount", "0");
        body.put("approval_url", approvalUrl);
        body.put("cancel_url", cancelUrl);
        body.put("fail_url", paymentFailUrl);

        HttpHeaders headers = httpHeaders();
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

                savedPayment.updateTid(tid);
                paymentRepository.save(savedPayment);

                return new PaymentReadyResDto(redirectUrl);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 준비 응답 처리 중 오류 발생");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 준비 요청 실패");
        }
    }

    public Payment savePayment(Reservation reservation, Long partnerUserId, Integer totalPrice) {
        Payment payment = new Payment(reservation, partnerUserId, totalPrice);

        return paymentRepository.save(payment);
    }

    // 최종적으로 결제 완료 처리를 하는 단계
    @Transactional
    public PaymentCompletedResDto payCompleted(Long productId, Long reservationId, String pgToken) {
        Product product = productService.findProductById(productId);
        Payment payment = paymentRepository.findPaymentByReservationId(reservationId);
        Reservation reservation = reservationService.findReservationByUserIdAndProductIdAndId(payment.getUserId(), product.getId(), reservationId);

        Map<String, String> body = new HashMap<>();
        body.put("cid", Const.KAKAO_PAY_TEST_CID);
        body.put("tid", payment.getTid());
        body.put("partner_order_id", reservation.getId().toString());
        body.put("partner_user_id", payment.getUserId().toString());
        body.put("pg_token", pgToken);

        HttpHeaders headers = httpHeaders();
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
                Integer quantity = root.path("quantity").asInt();
                Integer total = root.path("total").asInt();

                PaymentCompletedResDto paymentCompletedResDto = new PaymentCompletedResDto(
                        aid,
                        tid,
                        partnerOrderId,
                        partnerUserId,
                        itemName,
                        quantity,
                        total
                );

                payment.updateStatusAndType(PaymentStatus.APPROVED, paymentType);
                paymentRepository.save(payment);

                return paymentCompletedResDto;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 승인 응답 처리 중 오류가 발생했습니다.");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 승인 요청 실패");
        }
    }

    @Transactional
    public PaymentCancelResDto payCancel(Long paymentId) {
        Payment payment = paymentRepository.findPaymentById(paymentId);
        User user = userService.findAuthenticatedUser();
        Reservation reservation = reservationService.findReservationByPaymentIdAndUserId(payment.getId(), user.getId());
        Integer totalPrice = payment.getTotalPrice();
        Integer cancelPrice;

        if (LocalDate.now().equals(reservation.getReservationDate()) || LocalDate.now().isAfter(reservation.getReservationDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 당일 혹은 이미 지난 예약은 결제를 취소할 수 없습니다.");
        }

        if (payment.getStatus().equals(PaymentStatus.CANCELED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 결제가 취소 처리되었습니다.");
        }

        if (LocalDate.now().equals(reservation.getReservationDate().minusDays(RefundPolicy.PARTIAL_REFUND.getDaysBefore()))) {
            cancelPrice = (int) Math.round(totalPrice * RefundPolicy.PARTIAL_REFUND.getRefundRate());
        } else {
            cancelPrice = totalPrice;
        }

        Map<String, String> body = new HashMap<>();
        body.put("cid", Const.KAKAO_PAY_TEST_CID);
        body.put("tid", payment.getTid());
        body.put("cancel_amount", String.valueOf(cancelPrice));
        body.put("cancel_tax_free_amount", "0");

        HttpHeaders headers = httpHeaders();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoPayCancelUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root = objectMapper.readTree(response.getBody());

                String status = root.path("status").asText();
                String partnerOrderId = root.path("partner_order_id").asText();
                String partnerUserId = root.path("partner_user_id").asText();
                String paymentType = root.path("payment_method_type").asText();
                String itemName = root.path("item_name").asText();
                Integer quantity = root.path("quantity").asInt();
                Integer total = root.path("approved_cancel_amount").path("total").asInt();
                String canceledAt = root.path("canceled_at").asText();

                PaymentCancelResDto paymentCancelResDto = new PaymentCancelResDto(
                        status,
                        partnerOrderId,
                        partnerUserId,
                        paymentType,
                        itemName,
                        quantity,
                        total,
                        canceledAt
                );

                if (!payment.getTotalPrice().equals(total)) {
                    payment.updateCancelInfo(PaymentStatus.CANCELED, RefundType.PARTIAL_REFUND, total);
                } else {
                    payment.updateCancelInfo(PaymentStatus.CANCELED, RefundType.FULL_REFUND, total);
                }

                paymentRepository.save(payment);

                return paymentCancelResDto;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("카카오페이 결제 승인 응답 처리 중 오류가 발생했습니다.");
            }
        } else {
            throw new RuntimeException("카카오페이 결제 승인 요청 실패");
        }
    }

    private HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, Const.KAKAO_PAY_HEADER + " " + secretKey);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }

    public Payment findPaymentById(Long paymentId) {
        return paymentRepository.findPaymentById(paymentId);
    }
}