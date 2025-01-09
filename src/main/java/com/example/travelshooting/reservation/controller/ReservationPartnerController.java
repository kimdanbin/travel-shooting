package com.example.travelshooting.reservation.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.service.ReservationPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partners/products/{productId}/reservations")
public class ReservationPartnerController {

    private final ReservationPartnerService reservationPartnerService;

    // 레저/티켓 예약 전체 조회
    @GetMapping
    public ResponseEntity<CommonListResDto<ReservationResDto>> findByProductIdAndUserId(@PathVariable Long productId) {
        List<ReservationResDto> reservations = reservationPartnerService.findByProductIdAndUserId(productId);

        return new ResponseEntity<>(new CommonListResDto<>("레저/티켓 예약 전체 조회", reservations), HttpStatus.OK);
    }

    // 레저/티켓 예약 단 건 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<CommonResDto<ReservationResDto>> findByProductIdAndUserIdAndId(@PathVariable Long productId,
                                                                                @PathVariable Long reservationId) {
        ReservationResDto reservation = reservationPartnerService.findByProductIdAndUserIdAndId(productId, reservationId);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 예약 단 건 조회", reservation), HttpStatus.OK);
    }
}
