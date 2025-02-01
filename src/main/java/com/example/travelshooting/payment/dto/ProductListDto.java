package com.example.travelshooting.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductListDto {

    private final Long productId;
    private final String productName;
    private final Integer productAmount;
    private final List<PaymentDto> payments;
    private final Integer refundAmount;
}
