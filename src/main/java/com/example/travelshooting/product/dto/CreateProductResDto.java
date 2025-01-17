package com.example.travelshooting.product.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    private final Integer quantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
