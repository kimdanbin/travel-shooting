package com.example.travelshooting.part.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class UpdatePartReqDto {

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "활동 시작 시간을 입력해주세요.")
    private final LocalTime openAt;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "활동 종료 시간을 입력해주세요.")
    private final LocalTime closeAt;

    @NotNull(message = "인원을 입력해주세요.")
    @Size(max = 999)
    private final Integer maxQuantity;

}
