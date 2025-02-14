package com.example.travelshooting.payment.repository;

import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentCustomRepository {

    default Payment findPaymentById(Long paymentId) {
        return findById(paymentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + paymentId + "에 해당하는 레저/티켓 결제 내역을 찾을 수 없습니다."));
    }

    Payment findPaymentByReservationId(Long reservationId);

    Payment findPaymentByReservationIdAndStatus(Long reservationId, PaymentStatus status);
}