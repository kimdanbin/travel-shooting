package com.example.travelshooting.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateCompanyReqDto {

    @NotBlank(message = "업체 설명을 입력해주세요.")
    private final String description;

}
