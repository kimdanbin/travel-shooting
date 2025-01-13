package com.example.travelshooting.payment;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.reservation.Reservation;
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

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private String tid;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status = PaymentStatus.READY;

    @Column(nullable = false)
    private int totalPrice;

    public Payment(Reservation reservation, String tid, int totalPrice) {
        this.reservation = reservation;
        this.tid = tid;
        this.totalPrice = totalPrice;
    }

    public void updatePayStatus(PaymentStatus status) {
        this.status = status;
    }
}
