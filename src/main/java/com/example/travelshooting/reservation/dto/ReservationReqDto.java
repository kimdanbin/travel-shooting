package com.example.travelshooting.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationReqDto {

    @NotNull(message = "일정을 입력해주세요.")
    private Long partId;

    @NotNull(message = "예약 날짜를 입력해주세요.")
    private LocalDate reservationDate;

    @NotNull(message = "인원 수를 입력해주세요.")
    private Integer number;
}