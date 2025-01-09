package com.example.travelshooting.reservation.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ReservationResDto implements BaseDtoDataType {

    private Long id;
    private Long userId;
    private Long productId;
    private Long partId;
    private LocalDate reservationDate;
    private int number;
    private int totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReservationResDto(Long id, Long userId, Long productId, Long partId, LocalDate reservationDate,
                             int number, int totalPrice, ReservationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.partId = partId;
        this.reservationDate = reservationDate;
        this.number = number;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
