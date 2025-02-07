package com.example.travelshooting.poster.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.poster.entity.Poster;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PosterResDto implements BaseDtoDataType {

    private final Long id;

    private final Long userId;

    private final Long restaurantId;

    private final Long paymentId;

    private final Integer expenses;

    private final String title;

    private final String content;

    private final LocalDateTime travelStartAt;

    private final LocalDateTime travelEndAt;

    private final long likes;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @QueryProjection
    public PosterResDto(Poster poster) {
        this.id = poster.getId();
        this.userId = poster.getUser().getId();
        this.restaurantId = poster.getRestaurant() != null ? poster.getRestaurant().getId() : null;
        this.paymentId = poster.getPayment().getId() != null ? poster.getPayment().getId() : null;
        this.expenses = poster.getExpenses();
        this.title = poster.getTitle();
        this.content = poster.getContent();
        this.likes = poster.getLikePosters().size();
        this.travelStartAt = poster.getTravelStartAt();
        this.travelEndAt = poster.getTravelEndAt();
        this.createdAt = poster.getCreatedAt();
        this.updatedAt = poster.getUpdatedAt();
    }
    @QueryProjection
    public PosterResDto(Long id, Long userId, Long restaurantId, Long paymentId, Integer expenses, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt, int likes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.paymentId = paymentId;
        this.expenses = expenses;
        this.title = title;
        this.content = content;
        this.travelStartAt = travelStartAt;
        this.travelEndAt = travelEndAt;
        this.likes = likes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
