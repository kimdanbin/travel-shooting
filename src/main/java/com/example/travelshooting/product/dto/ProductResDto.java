package com.example.travelshooting.product.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ProductResDto implements BaseDtoDataType {

    private final Long id;
    private final Long companyId;
    private final String name;
    private final String description;
    private final Integer price;
    private final String address;
    private final LocalDate saleStartAt;
    private final LocalDate saleEndAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public ProductResDto(Long id, Long companyId, String name, String description, Integer price, String address,
                         LocalDate saleStartAt, LocalDate saleEndAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.address =address;
        this.saleStartAt = saleStartAt;
        this.saleEndAt  = saleEndAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
