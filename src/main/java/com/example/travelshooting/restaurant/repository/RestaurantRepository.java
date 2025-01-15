package com.example.travelshooting.restaurant.repository;

import com.example.travelshooting.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    default Restaurant findRestaurantById(Long restaurantId) {
        return findById(restaurantId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + restaurantId + "에 해당하는 맛집을 찾을 수 없습니다."));
    }

    @Query("SELECT r FROM Restaurant r " +
            "INNER JOIN Poster p ON r.id = p.restaurant.id " +
            "WHERE (:placeName IS NULL OR r.placeName LIKE %:placeName%) " +
            "AND (:region IS NULL OR r.region LIKE %:region%) " +
            "AND (:city IS NULL OR r.city LIKE %:city%)")
    Page<Restaurant> findAllById(@Param("placeName") String placeName,
                                       @Param("region") String region,
                                       @Param("city") String city,
                                       Pageable pageable);
}
