package com.example.travelshooting.product.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.part.dto.PartResDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductDetailResDto implements BaseDtoDataType {

    private final Long id;
    private final Long companyId;
    private final String name;
    private final String description;
    private final int price;
    private final String address;
    private final int quantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<PartResDto> parts;

}
