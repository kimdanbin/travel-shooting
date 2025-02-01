package com.example.travelshooting.payment.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SettlementResDto implements BaseDtoDataType {

    private final List<ProductListDto> products;
    private final Integer settlementAmount;
}
