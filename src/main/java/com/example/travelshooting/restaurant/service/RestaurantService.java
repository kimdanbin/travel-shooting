package com.example.travelshooting.restaurant.service;

import com.example.travelshooting.restaurant.dto.RestaurantResDto;
import com.example.travelshooting.restaurant.entity.Restaurant;
import com.example.travelshooting.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public Page<RestaurantResDto> findAllById(String placeName, String region, String city, Pageable pageable) {
        return restaurantRepository.findAllById(placeName, region, city, pageable)
                .map(restaurant -> new RestaurantResDto (
                        restaurant.getId(),
                        restaurant.getRegion(),
                        restaurant.getCity(),
                        restaurant.getPlaceName(),
                        restaurant.getAddressName(),
                        restaurant.getRoadAddressName(),
                        restaurant.getPhone(),
                        restaurant.getLongitude(),
                        restaurant.getLatitude()
                ));
    }

    public Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findRestaurantById(restaurantId);
    }
}
