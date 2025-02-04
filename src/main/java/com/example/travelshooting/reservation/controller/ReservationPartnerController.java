package com.example.travelshooting.reservation.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.dto.ReservationStatusReqDto;
import com.example.travelshooting.reservation.service.ReservationPartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partners/products/{productId}/reservations")
@RequiredArgsConstructor
public class ReservationPartnerController {

    private final ReservationPartnerService reservationPartnerService;

    // 레저/티켓 예약 전체 조회
    @GetMapping
    public ResponseEntity<CommonListResDto<ReservationResDto>> findAllByProductIdAndUserId(@PathVariable Long productId, Pageable pageable) {
        Page<ReservationResDto> reservations = reservationPartnerService.findAllByProductIdAndUserId(productId, pageable);
        List<ReservationResDto> content = reservations.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("레저/티켓 예약 전체 조회 완료", content), HttpStatus.OK);
    }

    // 레저/티켓 예약 단 건 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<CommonResDto<ReservationResDto>> findReservationByProductIdAndId(@PathVariable Long productId,
                                                                                           @PathVariable Long reservationId) {
        ReservationResDto reservation = reservationPartnerService.findPartnerReservationByProductIdAndId(productId, reservationId);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 예약 단 건 조회 완료", reservation), HttpStatus.OK);
    }

    // 예약 상태 변경
    @PatchMapping("/{reservationId}")
    public ResponseEntity<CommonResDto<ReservationResDto>> updateReservationStatus(@PathVariable Long productId,
                                                                                   @PathVariable Long reservationId,
                                                                                   @Valid @RequestBody ReservationStatusReqDto reservationStatusReqDto) {
        ReservationResDto reservation = reservationPartnerService.updateReservationStatus(productId, reservationId, reservationStatusReqDto.getStatus());

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 예약 상태 변경 완료", reservation), HttpStatus.OK);
    }
}
