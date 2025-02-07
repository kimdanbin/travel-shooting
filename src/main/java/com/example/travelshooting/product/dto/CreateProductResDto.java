package com.example.travelshooting.product.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateProductResDto implements BaseDtoDataType {

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
}
