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

    @Column(nullable = false, length = 10)
    private String region;
    
    @Column(nullable = false, length = 10)
    private String city;

    @Column(nullable = false, length = 30)
    private String placeName;

    @Column(nullable = false, length = 50)
    private String addressName;

    @Column(nullable = false, length = 50)
    private String roadAddressName;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String longitude; // 경도

    @Column(nullable = false, length = 100)
    private String latitude; // 위도

    private String imageUrl;

    @OneToMany(mappedBy = "restaurant")
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

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
