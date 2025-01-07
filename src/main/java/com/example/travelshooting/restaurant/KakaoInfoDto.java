package com.example.travelshooting.restaurant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoInfoDto {

    private String place_name;
    private String address_name;
    private String road_address_name;
    private String phone;
    private String x;
    private String y;
}
