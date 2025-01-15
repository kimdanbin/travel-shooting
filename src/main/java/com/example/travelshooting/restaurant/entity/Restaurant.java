package com.example.travelshooting.restaurant.entity;

import com.example.travelshooting.poster.entity.Poster;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Getter
@NoArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String region;
    private String city;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String longitude; // 경도
    private String latitude; // 위도

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Poster> posters = new ArrayList<>();

    public Restaurant(String region, String city, String placeName, String addressName, String roadAddressName, String phone, String longitude, String latitude) {
        this.region = region;
        this.city = city;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
