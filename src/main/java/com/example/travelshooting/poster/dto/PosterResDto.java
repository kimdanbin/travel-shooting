package com.example.travelshooting.poster.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.poster.entity.Poster;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PosterResDto implements BaseDtoDataType {

    private final Long id;

    private final Long userId;

    private final Long restaurantId;

//    private final Long paymentId;

    private final String title;

    private final String content;

    private final LocalDateTime travelStartAt;

    private final LocalDateTime travelEndAt;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public PosterResDto(Poster poster) {
        this.id = poster.getId();
        this.userId = poster.getUser().getId();
        if (poster.getRestaurant() != null) {
            this.restaurantId = poster.getRestaurant().getId();
        } else {
            this.restaurantId = null;
        }
//        this.paymentId = poster.getPayment().getId(); 나중에 추가
        this.title = poster.getTitle();
        this.content = poster.getContent();
        this.travelStartAt = poster.getTravelStartAt();
        this.travelEndAt = poster.getTravelEndAt();
        this.createdAt = poster.getCreatedAt();
        this.updatedAt = poster.getUpdatedAt();
    }

}
