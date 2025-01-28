package com.example.travelshooting.reservation.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.entity.Notification;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.payment.entity.Payment;
import com.example.travelshooting.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor
@Where(clause = "is_deleted = false")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_part_id", nullable = false)
    private Part part;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private Integer headCount;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "reservation")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "reservation")
    private List<Notification> notifications = new ArrayList<>();

    public Reservation(User user, Part part, LocalDate reservationDate, Integer headCount, Integer totalPrice) {
        this.user = user;
        this.part = part;
        this.reservationDate = reservationDate;
        this.headCount = headCount;
        this.totalPrice = totalPrice;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void updateReservation(ReservationStatus status) {
        this.isDeleted = true;
        this.status = status;
    }
}