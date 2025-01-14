package com.example.travelshooting.part.dto;

import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class CreatePartReqDto {

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "활동 시작 시간은 필수 입력 항목입니다.")
    private final LocalTime openAt;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "활동 종료 시간은 필수 입력 항목입니다.")
    private final LocalTime closeAt;

    @NotNull(message = "인원은 필수 입력 항목입니다.")
    private final int number;

    public Part toEntity(Product product) {
        return new Part(
                this.openAt,
                this.closeAt,
                this.number,
                product
        );
    }

}
