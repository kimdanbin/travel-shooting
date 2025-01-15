package com.example.travelshooting.restaurant.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.restaurant.dto.RestaurantResDto;
import com.example.travelshooting.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants/search")
@RequiredArgsConstructor
public class RestaurantSearchController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<CommonListResDto<RestaurantResDto>> findAllById(@RequestParam(required = false) String placeName,
                                                                      @RequestParam(required = false) String region,
                                                                      @RequestParam(required = false) String city,
                                                                      Pageable pageable) {
        Page<RestaurantResDto> restaurants = restaurantService.findAllById(placeName, region, city, pageable);
        List<RestaurantResDto> content = restaurants.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("맛집 검색 완료", content), HttpStatus.OK);
    }
}
