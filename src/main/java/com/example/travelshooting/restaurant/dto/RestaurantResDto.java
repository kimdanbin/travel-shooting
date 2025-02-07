package com.example.travelshooting.restaurant.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class RestaurantResDto implements BaseDtoDataType {

    private final Long id;
    private final String region;
    private final String city;
    private final String placeName;
    private final String addressName;
    private final String roadAddressName;
    private final String phone;
    private final String longitude;
    private final String latitude;
    private final String imageUrl;

    @QueryProjection
    public RestaurantResDto(Long id, String region, String city, String placeName,
                            String addressName, String roadAddressName, String phone,
                            String longitude, String latitude, String imageUrl) {
        this.id = id;
        this.region = region;
        this.city = city;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
        this.imageUrl = imageUrl;
    }
}
