package com.example.travelshooting.restaurant.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.restaurant.dto.GgRestaurantResDto;
import com.example.travelshooting.restaurant.dto.RestaurantResDto;
import com.example.travelshooting.restaurant.dto.RestaurantSearchResDto;
import com.example.travelshooting.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 경기도 맛집 정보 저장
    @PostMapping("/regions/gyeonggi")
    public ResponseEntity<CommonListResDto<GgRestaurantResDto>> saveRestaurants(@RequestParam int pIndex,
                                                                                @RequestParam int pSize) {
        List<GgRestaurantResDto> result = restaurantService.saveGgRestaurants(pIndex, pSize);

        return new ResponseEntity<>(new CommonListResDto<>("경기도 맛집 정보 저장 완료", result), HttpStatus.CREATED);
    }

    // 맛집 검색
    @GetMapping("/search")
    public ResponseEntity<CommonListResDto<RestaurantSearchResDto>> findAllById(@RequestParam(required = false) String placeName,
                                                                                @RequestParam(required = false) String region,
                                                                                @RequestParam(required = false) String city,
                                                                                @PageableDefault(size = 10, page = 0, sort = "placeName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RestaurantSearchResDto> restaurants = restaurantService.findAllById(placeName, region, city, pageable);
        List<RestaurantSearchResDto> content = restaurants.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("맛집 검색 완료", content), HttpStatus.OK);
    }

    // 맛집 대표 이미지 추가
    @PostMapping("/{restaurantId}/attachments")
    public ResponseEntity<CommonResDto<RestaurantResDto>> uploadFile(@PathVariable Long restaurantId,
                                                                     @RequestParam MultipartFile file) {
        RestaurantResDto restaurant = restaurantService.uploadFile(restaurantId, file);

        return new ResponseEntity<>(new CommonResDto<>("파일 업로드 완료", restaurant), HttpStatus.OK);
    }
}