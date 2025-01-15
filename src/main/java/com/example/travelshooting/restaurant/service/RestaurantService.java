package com.example.travelshooting.restaurant.service;

import com.example.travelshooting.restaurant.entity.Restaurant;
import com.example.travelshooting.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findRestaurantById(restaurantId);
    }
}
