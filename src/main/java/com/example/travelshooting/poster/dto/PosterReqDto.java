package com.example.travelshooting.poster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PosterReqDto {

    private Long restaurantId;

    private Long paymentId;

    @NotNull(message = "여행경비를 입력해주세요")
    private Integer expenses;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "여행 시작 날짜를 입력해주세요.")
    private LocalDateTime travelStartAt;

    @NotNull(message = "여행 종료 날짜를 입력해주세요.")
    private LocalDateTime travelEndAt;

    public PosterReqDto(Long restaurantId, Long paymentId, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt) {
        this.restaurantId = restaurantId;
        this.paymentId = paymentId;
        this.title = title;
        this.content = content;
        this.travelStartAt = travelStartAt;
        this.travelEndAt = travelEndAt;
    }
}
