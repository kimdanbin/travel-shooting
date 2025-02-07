package com.example.travelshooting.report.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.report.dto.ReportReqDto;
import com.example.travelshooting.report.dto.ReportResDto;
import com.example.travelshooting.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posters/{posterId}")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 포스터 신고 API
     *
     * @param posterId 신고하려는 포스터의 id
     * @param reportReqDto 신고 사유를 담고 있는 dto
     * @return 신고 정보를 담고 있는 dto. 성공시 상태코드 200 반환
     */
    @PostMapping("/reports")
    public ResponseEntity<CommonResDto<ReportResDto>> reportPoster (
            @PathVariable Long posterId,
            @Valid @RequestBody ReportReqDto reportReqDto
            ) {
        ReportResDto result = reportService.reportPoster(posterId, reportReqDto.getReason());

        return new ResponseEntity<>(new CommonResDto<>("포스터 신고 완료", result), HttpStatus.OK);
    }

    /**
     * 댓글 신고 API
     *
     * @param commentId 신고하려는 댓글의 id
     * @param reportReqDto 신고 사유를 담고 있는 dto
     * @return 신고 정보를 담고 있는 dto. 성공시 상태코드 200 반환
     */
    @PostMapping("/comments/{commentId}/reports")
    public ResponseEntity<CommonResDto<ReportResDto>> reportComment(
            @PathVariable Long posterId,
            @PathVariable Long commentId,
            @Valid @RequestBody ReportReqDto reportReqDto
    ) {
        ReportResDto result = reportService.reportComment(posterId, commentId, reportReqDto.getReason());

        return new ResponseEntity<>(new CommonResDto<>("댓글 신고 완료", result), HttpStatus.OK);
    }

}
