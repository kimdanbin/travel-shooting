package com.example.travelshooting.reservation.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.reservation.dto.ReservationReqDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약
    @PostMapping
    public ResponseEntity<CommonResDto<ReservationResDto>> createReservation(@PathVariable Long productId,
                                                                            @Valid @RequestBody ReservationReqDto reservationReqDto) {
        ReservationResDto reservation = reservationService.createReservation(productId, reservationReqDto.getPartId(), reservationReqDto.getReservationDate(), reservationReqDto.getHeadCount());

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 예약 신청 완료", reservation), HttpStatus.CREATED);
    }

    // 예약 취소
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long productId,
                                                    @PathVariable Long reservationId) {
        reservationService.deleteReservation(productId, reservationId);

        return new ResponseEntity<>("레저/티켓 예약 취소 완료", HttpStatus.OK);
    }

    // 예약 전체 조회
    @GetMapping
    public ResponseEntity<CommonListResDto<ReservationResDto>> findAllByUserIdAndProductId(@PathVariable Long productId, Pageable pageable) {
        Page<ReservationResDto> reservations = reservationService.findAllByUserIdAndProductId(productId, pageable);
        List<ReservationResDto> content = reservations.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("레저/티켓 예약 전체 조회 완료", content), HttpStatus.OK);
    }

    // 예약 단 건 조회
    @GetMapping ("/{reservationId}")
    ResponseEntity<CommonResDto<ReservationResDto>> findReservationByUserIdAndProductIdAndId(@PathVariable Long productId,
                                                                             @PathVariable Long reservationId) {
        ReservationResDto reservation = reservationService.findReservationByUserIdAndProductIdAndId(productId, reservationId);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 예약 단 건 조회 완료", reservation), HttpStatus.OK);
    }
}
