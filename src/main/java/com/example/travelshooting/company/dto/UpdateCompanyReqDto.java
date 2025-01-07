package com.example.travelshooting.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateCompanyReqDto {

    @NotBlank(message = "업체 설명은 필수 입력 항목입니다.")
    private final String description;

}
