package com.example.travelshooting.report.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.report.dto.ReportReqDto;
import com.example.travelshooting.report.dto.ReportResDto;
import com.example.travelshooting.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    /**
     * 포스터 신고 API
     *
     * @param posterId 신고하려는 포스터의 id
     * @param reportReqDto 신고 사유를 담고 있는 dto
     * @return 신고 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @PostMapping("/posters/{posterId}/reports")
    public ResponseEntity<CommonResDto<ReportResDto>> reportPoster (
            @PathVariable Long posterId,
            @Valid @RequestBody ReportReqDto reportReqDto
            ) {
        ReportResDto result = reportService.reportPoster(posterId, reportReqDto.getReason());

        return new ResponseEntity<>(new CommonResDto<>("포스터 신고 완료", result), HttpStatus.CREATED);
    }

    /**
     * 댓글 신고 API
     *
     * @param commentId 신고하려는 댓글의 id
     * @param reportReqDto 신고 사유를 담고 있는 dto
     * @return 신고 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @PostMapping("/posters/{posterId}/comments/{commentId}/reports")
    public ResponseEntity<CommonResDto<ReportResDto>> reportComment(
            @PathVariable Long commentId,
            @Valid @RequestBody ReportReqDto reportReqDto
    ) {
        ReportResDto result = reportService.reportComment(commentId, reportReqDto.getReason());

        return new ResponseEntity<>(new CommonResDto<>("댓글 신고 완료", result), HttpStatus.CREATED);
    }

    /**
     * 신고 내역 전체 조회 API
     *
     * @return 전체 신고 내역. 성공시 상태코드 200 반환
     */
    @GetMapping("/admins/reports")
    public ResponseEntity<CommonListResDto<ReportResDto>> findAllReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        List<ReportResDto>result = reportService.findAllReports(page, size);

        return new ResponseEntity<>(new CommonListResDto<>("신고 전체 조회 완료", result), HttpStatus.OK);
    }

    /**
     * 신고 내역 단 건 조회 API
     *
     * @param reportId 조회할 신고 id
     * @return 조회된 신고 내역. 성공시 상태코드 200 반환
     */
    @GetMapping("/admins/reports/{reportId}")
    public ResponseEntity<CommonResDto<ReportResDto>> findReport(@PathVariable Long reportId) {
        ReportResDto result = reportService.findReport(reportId);

        return new ResponseEntity<>(new CommonResDto<>("신고 전체 조회 완료", result), HttpStatus.OK);
    }

}
