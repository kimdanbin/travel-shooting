package com.example.travelshooting.poster.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.poster.dto.PosterReqDto;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.service.PosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posters")
public class PosterController {
    private final PosterService posterService;

    // 포스터 생성
    @PostMapping()
    public ResponseEntity<CommonResDto<PosterResDto>> createPoster(
            @RequestBody PosterReqDto posterReqDto
    ) {

        PosterResDto posterResDto = posterService.createPoster(
                posterReqDto.getRestaurantId(),
                posterReqDto.getPaymentId(),
                posterReqDto.getTitle(),
                posterReqDto.getContent(),
                posterReqDto.getTravelStartAt(),
                posterReqDto.getTravelEndAt()
        );

        return new ResponseEntity<>(new CommonResDto<>("포스터 생성 완료", posterResDto), HttpStatus.CREATED);
    }

    // 포스터 단건 조회
    @GetMapping("/{posterId}")
    public ResponseEntity<CommonResDto<PosterResDto>> findPoster(
            @PathVariable Long posterId
    ) {

        PosterResDto posterResDto = posterService.findPoster(posterId);

        return new ResponseEntity<>(new CommonResDto<>("포스터 단 건 조회 완료", posterResDto), HttpStatus.OK);
    }

    // 포스터 수정
    @PatchMapping("/{posterId}")
    public ResponseEntity<CommonResDto<PosterResDto>> updatePoster(
            @PathVariable Long posterId,
            @RequestBody PosterReqDto posterReqDto
    ) {

        PosterResDto posterResDto = posterService.updatePoster(
                posterId,
                posterReqDto.getRestaurantId(),
                posterReqDto.getPaymentId(),
                posterReqDto.getTitle(),
                posterReqDto.getContent(),
                posterReqDto.getTravelStartAt(),
                posterReqDto.getTravelEndAt()
        );

        return new ResponseEntity<>(new CommonResDto<>("포스터 수정 완료", posterResDto), HttpStatus.OK);
    }

    // 포스터 삭제
    @DeleteMapping("/{posterId}")
    public ResponseEntity<String> deletePoster(
            @PathVariable Long posterId
    ) {

        posterService.deletePoster(posterId);

        return new ResponseEntity<>("포스터 삭제 완료", HttpStatus.NO_CONTENT);
    }

}
