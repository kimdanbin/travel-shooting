package com.example.travelshooting.reservation;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.product.Product;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "leisure_product_id")
    private Product product;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    private int number;

    private int totalPrice;

    private ReservationStatus status;

}
