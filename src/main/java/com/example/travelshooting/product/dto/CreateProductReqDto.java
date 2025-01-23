package com.example.travelshooting.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CreateProductReqDto {

    @NotBlank(message = "상품명을 입력해주세요.")
    private final String name;

    @NotBlank(message = "상품 설명을 입력해주세요.")
    private final String description;

    @NotNull(message = "가격을 입력해주세요.")
    private final Integer price;

    @NotBlank(message = "활동 지역 주소를 입력해주세요.")
    private final String address;

//    @NotNull(message = "판매 개수를 입력해주세요.")
//    private final Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "판매 시작 일자를 입력해주세요.")
    private LocalDate saleStartAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "판매 종료 일자를 입력해주세요.")
    private LocalDate saleEndAt;

}
