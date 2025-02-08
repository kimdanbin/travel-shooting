package com.example.travelshooting.part.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class PartResDto implements BaseDtoDataType{

    private final Long id;

    private final LocalTime openAt;

    private final LocalTime closeAt;

    private final Integer maxQuantity;

}
