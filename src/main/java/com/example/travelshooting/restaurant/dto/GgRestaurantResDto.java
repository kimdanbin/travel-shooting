package com.example.travelshooting.restaurant.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.restaurant.Restaurant;
import lombok.Getter;

@Getter
public class GgRestaurantResDto implements BaseDtoDataType {

    private Long id;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String longitude;
    private String latitude;

    public GgRestaurantResDto(Long id, String placeName, String addressName, String roadAddressName, String phone, String longitude, String latitude) {
        this.id = id;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static GgRestaurantResDto toDto(Restaurant restaurant) {
        return new GgRestaurantResDto(
                restaurant.getId(),
                restaurant.getPlaceName(),
                restaurant.getAddressName(),
                restaurant.getRoadAddressName(),
                restaurant.getPhone(),
                restaurant.getLongitude(),
                restaurant.getLatitude()
        );
    }
}
