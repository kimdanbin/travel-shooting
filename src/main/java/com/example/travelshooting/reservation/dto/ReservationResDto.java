package com.example.travelshooting.reservation.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.ReservationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
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
}
