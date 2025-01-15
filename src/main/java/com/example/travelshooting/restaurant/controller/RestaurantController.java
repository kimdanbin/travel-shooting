package com.example.travelshooting.restaurant.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.restaurant.service.GgRestaurantService;
import com.example.travelshooting.restaurant.dto.GgRestaurantResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/restaurants/regions")
public class RestaurantController {

    private final GgRestaurantService ggRestaurantService;

    // 경기도 맛집 정보 저장
    @PostMapping("/gyeonggi")
    public ResponseEntity<CommonListResDto<GgRestaurantResDto>> saveRestaurants(@RequestParam int pIndex, @RequestParam int pSize) {
        List<GgRestaurantResDto> result = ggRestaurantService.saveGgRestaurants(pIndex, pSize);

        return new ResponseEntity<>(new CommonListResDto<>("경기도 맛집 정보 저장 완료", result), HttpStatus.CREATED);
    }
}