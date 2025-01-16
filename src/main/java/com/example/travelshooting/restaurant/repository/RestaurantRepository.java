package com.example.travelshooting.restaurant.repository;

import com.example.travelshooting.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    default Restaurant findRestaurantById(Long restaurantId) {
        return findById(restaurantId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + restaurantId + "에 해당하는 맛집을 찾을 수 없습니다."));
    }
}
