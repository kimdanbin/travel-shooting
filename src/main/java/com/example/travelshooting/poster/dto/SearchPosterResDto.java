package com.example.travelshooting.poster.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class SearchPosterResDto implements BaseDtoDataType {
  private final Long id;
  private final String title;
  private final Integer expenses;
  private final LocalDateTime travelStartAt;
  private final LocalDateTime travelEndAt;
  private final Integer likeCount;

  @QueryProjection
  public SearchPosterResDto(Long id, String title, Integer expenses, LocalDateTime travelStartAt, LocalDateTime travelEndAt, Integer likeCount) {
    this.id = id;
    this.title = title;
    this.expenses = expenses;
    this.travelStartAt = travelStartAt;
    this.travelEndAt = travelEndAt;
    this.likeCount = likeCount;
  }
}

