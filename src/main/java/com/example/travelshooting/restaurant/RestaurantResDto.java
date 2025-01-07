package com.example.travelshooting.restaurant;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;

@Getter
public class RestaurantResDto implements BaseDtoDataType {

    private Long id;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String longitude;
    private String latitude;

    public RestaurantResDto(Long id, String placeName, String addressName, String roadAddressName, String phone, String longitude, String latitude) {
        this.id = id;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static RestaurantResDto toDto(Restaurant restaurant) {
        return new RestaurantResDto(
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
