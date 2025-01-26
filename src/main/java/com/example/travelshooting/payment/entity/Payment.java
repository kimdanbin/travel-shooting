package com.example.travelshooting.payment.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.RefundType;
import com.example.travelshooting.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservaiton_id", nullable = false)
    private Reservation reservation;

    private String tid;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status = PaymentStatus.READY;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(length = 10)
    private String type;

    private Integer cancelPrice = 0;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RefundType refundType = RefundType.NO_REFUND;

    public Payment(Reservation reservation, Long userId, Integer totalPrice) {
        this.reservation = reservation;
        this.userId = userId;
        this.totalPrice = totalPrice;
    }

    public void updateTid(String tid) {
        this.tid = tid;
    }

    public void updateStatusAndType(PaymentStatus status, String type) {
        this.status = status;
        this.type = type;
    }

    public void updateCancelInfo(PaymentStatus status, RefundType refundType, Integer cancelPrice) {
        this.status = status;
        this.refundType = refundType;
        this.cancelPrice = cancelPrice;
    }
}
