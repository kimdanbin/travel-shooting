package com.example.travelshooting.restaurant;

import com.example.travelshooting.common.CommonListResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<CommonListResDto<RestaurantResDto>> saveRestaurants() {
        List<RestaurantResDto> result = restaurantService.saveRestaurants();
        return new ResponseEntity<>(new CommonListResDto<>("음식점 정보 저장 완료", result), HttpStatus.CREATED);
    }
}