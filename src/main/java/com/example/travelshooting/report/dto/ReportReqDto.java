package com.example.travelshooting.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportReqDto {

    @NotBlank(message = "신고 사유를 입력해주세요.")
    private final String reason;
}
