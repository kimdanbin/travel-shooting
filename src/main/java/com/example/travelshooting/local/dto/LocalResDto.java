package com.example.travelshooting.local.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocalResDto implements BaseDtoDataType {

    private final Long id;

    private final String categoryName;

    private final String placeName;

    private final String addressName;

    private final String roadAddressName;

    private final String phone;

    private final String longitude; // 경도

    private final String latitude; // 위도
}
