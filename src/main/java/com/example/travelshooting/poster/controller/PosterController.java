package com.example.travelshooting.poster.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.poster.dto.PosterReqDto;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.service.PosterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/posters")
@RequiredArgsConstructor
public class PosterController {

    private final PosterService posterService;

    // 포스터 생성
    @PostMapping()
    public ResponseEntity<CommonResDto<PosterResDto>> createPoster(
            @Valid @RequestBody PosterReqDto posterReqDto
    ) {

        PosterResDto posterResDto = posterService.createPoster(
                posterReqDto.getRestaurantId(),
                posterReqDto.getPaymentId(),
                posterReqDto.getExpenses(),
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

    // 포스터 전체 조회
    @GetMapping("/search")
    public ResponseEntity<CommonListResDto<PosterResDto>> findPosters(@RequestParam(required = false) Integer minExpenses,
                                                                  @RequestParam(required = false) Integer maxExpenses,
                                                                  @RequestParam(required = false) LocalDate travelStartAt,
                                                                  @RequestParam(required = false) LocalDate travelEndAt,
                                                                  @RequestParam(required = false) Integer days,
                                                                  @RequestParam(required = false) Integer month,
                                                                  Pageable pageable) {
        Page<PosterResDto> posters = posterService.findPosters(minExpenses, maxExpenses, travelStartAt, travelEndAt, days, month, pageable);
        List<PosterResDto> content = posters.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("여행 코스 검색 완료", content), HttpStatus.OK);
    }

    // 포스터 수정
    @PatchMapping("/{posterId}")
    public ResponseEntity<CommonResDto<PosterResDto>> updatePoster(
            @PathVariable Long posterId,
            @Valid @RequestBody PosterReqDto posterReqDto
    ) {

        PosterResDto posterResDto = posterService.updatePoster(
                posterId,
                posterReqDto.getRestaurantId(),
                posterReqDto.getPaymentId(),
                posterReqDto.getExpenses(),
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

        return new ResponseEntity<>("포스터 삭제 완료", HttpStatus.OK);
    }
}
