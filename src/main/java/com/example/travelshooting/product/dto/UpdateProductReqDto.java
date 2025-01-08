package com.example.travelshooting.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateProductReqDto {

    @NotBlank(message = "업체 설명은 필수 입력 항목입니다.")
    private final String description;

    @NotNull(message = "유저 아이디는 필수 입력 항목입니다.")
    private final int price;

    @NotBlank(message = "업체 설명은 필수 입력 항목입니다.")
    private final String address;

    @NotNull(message = "유저 아이디는 필수 입력 항목입니다.")
    private final int quantity;

}
