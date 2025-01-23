package com.example.travelshooting.report.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.report.dto.ReportResDto;
import com.example.travelshooting.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins/reports")
@RequiredArgsConstructor
public class ReportAdminController {

    private final ReportService reportService;

    /**
     * 신고 내역 전체 조회 API
     *
     * @return 전체 신고 내역. 성공시 상태코드 200 반환
     */
    @GetMapping
    public ResponseEntity<CommonListResDto<ReportResDto>> findAllReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        List<ReportResDto> result = reportService.findAllReports(page, size);

        return new ResponseEntity<>(new CommonListResDto<>("신고 전체 조회 완료", result), HttpStatus.OK);
    }

    /**
     * 신고 내역 단 건 조회 API
     *
     * @param reportId 조회할 신고 id
     * @return 조회된 신고 내역. 성공시 상태코드 200 반환
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<CommonResDto<ReportResDto>> findReport(@PathVariable Long reportId) {
        ReportResDto result = reportService.findReport(reportId);

        return new ResponseEntity<>(new CommonResDto<>("신고 전체 조회 완료", result), HttpStatus.OK);
    }
}
