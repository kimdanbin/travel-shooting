package com.example.travelshooting.reservation.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ReservationStatusReqDto {

    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "APPROVED 또는 REJECTED만 허용합니다.")
    String status;
}
