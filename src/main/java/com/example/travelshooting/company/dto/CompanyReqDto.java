package com.example.travelshooting.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyReqDto {

    @NotNull(message = "유저 아이디를 입력해주세요.")
    private final Long userId;

    @NotBlank(message = "업체명을 입력해주세요.")
    private final String name;

    @NotBlank(message = "업체 설명을 입력해주세요.")
    private final String description;

}
