package com.example.travelshooting.product.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.part.dto.PartResDto;
import com.example.travelshooting.product.entity.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static ProductDetailResDto toDto(Product product) {
        List<PartResDto> parts = product.getParts().stream()
                .map(part -> new PartResDto(part.getId(), part.getOpenAt(), part.getCloseAt(), part.getNumber()))
                .collect(Collectors.toList());
        return new ProductDetailResDto(
                product.getId(),
                product.getCompany().getId(),
                product.getCompany().getName(),
                product.getCompany().getDescription(),
                product.getPrice(),
                product.getAddress(),
                product.getQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                parts
        );
    }
}
