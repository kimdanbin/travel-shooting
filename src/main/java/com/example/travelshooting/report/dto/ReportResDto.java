package com.example.travelshooting.report.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ReportResDto implements BaseDtoDataType {

    private final Long id;
    private final Long userId;
    private final ReportType type;
    private final Long fkId;
    private final String reason;
    private final LocalDateTime createdAt;

}
