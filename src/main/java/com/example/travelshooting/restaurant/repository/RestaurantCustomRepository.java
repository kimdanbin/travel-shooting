package com.example.travelshooting.restaurant.repository;

import com.example.travelshooting.restaurant.dto.RestaurantSearchResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantCustomRepository {

    Page<RestaurantSearchResDto> findRestaurants(String placeName, String region, String city, Pageable pageable);
}
