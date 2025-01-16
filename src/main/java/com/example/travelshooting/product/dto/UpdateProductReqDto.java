package com.example.travelshooting.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateProductReqDto {

    @NotBlank(message = "업체 설명을 입력해주세요")
    private final String description;

    @NotNull(message = "가격을 입력해주세요.")
    private final int price;

    @NotBlank(message = "활동 지역 주소를 입력해주세요.")
    private final String address;

    @NotNull(message = "판매 개수를 입력해주세요.")
    private final int quantity;

}
