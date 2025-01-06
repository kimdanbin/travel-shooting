package com.example.travelshooting.local;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocalResDto {

    private Long id;
    private String categoryName;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String longitude; // 경도
    private String latitude; // 위도
}
