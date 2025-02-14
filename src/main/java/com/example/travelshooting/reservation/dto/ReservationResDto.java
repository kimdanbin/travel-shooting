package com.example.travelshooting.reservation.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.ReservationStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ReservationResDto implements BaseDtoDataType {

    private final Long id;

    private final Long userId;

    private final Long productId;

    private final Long partId;

    private final LocalDate reservationDate;

    private final Integer headCount;

    private final Integer totalPrice;

    private final ReservationStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @QueryProjection
    public ReservationResDto(Long id, Long userId, Long productId, Long partId,
                             LocalDate reservationDate, Integer headCount, Integer totalPrice,
                             ReservationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.partId = partId;
        this.reservationDate = reservationDate;
        this.headCount = headCount;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
