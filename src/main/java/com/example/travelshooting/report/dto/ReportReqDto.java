package com.example.travelshooting.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportReqDto {

    @NotBlank(message = "신고 사유는 필수 입력 항목입니다.")
    private final String reason;

}
