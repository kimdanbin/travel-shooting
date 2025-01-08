package com.example.travelshooting.reservation.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.reservation.dto.ReservationReqDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // 예약
    @PostMapping
    public ResponseEntity<CommonResDto<ReservationResDto>> createReservation(@PathVariable Long productId,
                                                                            @Valid @RequestBody ReservationReqDto reservationReqDto) {
        ReservationResDto reservation = reservationService.createReservation(productId, reservationReqDto.getPartId(), reservationReqDto.getReservationDate(), reservationReqDto.getNumber());

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 예약 신청 완료", reservation), HttpStatus.CREATED);
    }
}
