package com.example.travelshooting.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GgRestaurantApiDto {

    @JsonProperty("SIGUN_NM")
    private String city;

    @JsonProperty("RESTRT_NM")
    private String placeName;

    @JsonProperty("REFINE_LOTNO_ADDR")
    private String addressName;

    @JsonProperty("REFINE_ROADNM_ADDR")
    private String roadAddressName;

    @JsonProperty("TASTFDPLC_TELNO")
    private String phone;

    @JsonProperty("REFINE_WGS84_LOGT")
    private String longitude;

    @JsonProperty("REFINE_WGS84_LAT")
    private String latitude;
}
