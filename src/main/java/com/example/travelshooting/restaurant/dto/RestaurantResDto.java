package com.example.travelshooting.restaurant.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;

@Getter
public class RestaurantResDto implements BaseDtoDataType {

    private Long id;
    private String region;
    private String city;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String longitude;
    private String latitude;
    private String imageUrl;

    public RestaurantResDto(Long id, String region, String city, String placeName, String addressName, String roadAddressName, String phone, String longitude, String latitude, String imageUrl) {
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
