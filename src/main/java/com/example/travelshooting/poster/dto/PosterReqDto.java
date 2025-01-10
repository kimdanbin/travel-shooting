package com.example.travelshooting.poster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class PosterReqDto {

    private Long restaurantId;

    private Long paymentId;

    private String title;

    private String content;

    private LocalDateTime travelStartAt;

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
