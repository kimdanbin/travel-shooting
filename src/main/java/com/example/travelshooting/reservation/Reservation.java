package com.example.travelshooting.reservation;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.payment.Payment;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE reservation SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "leisure_product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_part_id", nullable = false)
    private Part part;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "reservation")
    private List<Payment> payments = new ArrayList<>();

    public Reservation(User user, Product product, Part part, LocalDate reservationDate, int number, int totalPrice) {
        this.user = user;
        this.product = product;
        this.part = part;
        this.reservationDate = reservationDate;
        this.number = number;
        this.totalPrice = totalPrice;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}
