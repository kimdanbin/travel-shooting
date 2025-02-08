package com.example.travelshooting.restaurant.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GgRestaurantResDto implements BaseDtoDataType {

    private final Long id;

    private final String region;

    private final String city;

    private final String placeName;

    private final String addressName;

    private final String roadAddressName;

    private final String phone;

    private final String longitude;

    private final String latitude;
}
