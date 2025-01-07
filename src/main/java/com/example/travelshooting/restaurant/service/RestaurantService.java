package com.example.travelshooting.restaurant.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.restaurant.Restaurant;
import com.example.travelshooting.restaurant.dto.GgRestaurantApiDto;
import com.example.travelshooting.restaurant.dto.GgRestaurantResDto;
import com.example.travelshooting.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Value("${gg.api.food.key}")
    private String apiKey;

    private static final String GG_API_FOOD_URL = Const.GG_API_FOOD_URL;

    @Transactional
    public List<GgRestaurantResDto> saveGgRestaurants(int pIndex, int pSize) {
        String url = UriComponentsBuilder.fromHttpUrl(GG_API_FOOD_URL)
                .queryParam("KEY", apiKey)
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", pSize)
                .build()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String xmlData = restTemplate.getForObject(url, String.class);

        XmlMapper xmlMapper = new XmlMapper();
        List<GgRestaurantApiDto> apiDtos;

        try {
            apiDtos = xmlMapper.readValue(xmlData, xmlMapper.getTypeFactory().constructCollectionType(List.class, GgRestaurantApiDto.class));

            // 음식점명이 null인 항목 제거
            apiDtos = apiDtos.stream()
                    .filter(apiDto -> apiDto.getPlaceName() != null && !apiDto.getPlaceName().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("XML 파싱 오류가 발생했습니다.");
        }

        // placeName을 기준으로 중복 제거
        Map<String, GgRestaurantApiDto> uniqueMap = apiDtos.stream()
                .collect(Collectors.toMap(
                        GgRestaurantApiDto::getPlaceName,
                        apiDto -> apiDto,
                        (existing, replacement) -> existing // 중복된 키가 있으면 기존 값을 유지
                ));

        // 중복을 제거한 리스트
        List<GgRestaurantApiDto> uniqueApiDtos = new ArrayList<>(uniqueMap.values());

        List<Restaurant> savedRestaurants = uniqueApiDtos.stream()
                .map(apiDto -> {
                    Restaurant restaurant = new Restaurant(
                            apiDto.getPlaceName(),
                            apiDto.getAddressName(),
                            apiDto.getRoadAddressName(),
                            apiDto.getPhone(),
                            apiDto.getLongitude(),
                            apiDto.getLatitude()
                    );
                    return restaurantRepository.save(restaurant);
                })
                .collect(Collectors.toList());

        return savedRestaurants.stream()
                .map(GgRestaurantResDto::toDto)
                .collect(Collectors.toList());
    }
}
