package com.example.travelshooting.reservation.dto;

import com.example.travelshooting.enums.ReservationStatus;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ReservationStatusReqDto {

    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "수락 또는 거절만 허용합니다.")
    ReservationStatus status;
}
