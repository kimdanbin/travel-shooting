package com.example.travelshooting.restaurant.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.restaurant.dto.GgRestaurantApiDto;
import com.example.travelshooting.restaurant.dto.GgRestaurantResDto;
import com.example.travelshooting.restaurant.dto.RestaurantResDto;
import com.example.travelshooting.restaurant.dto.RestaurantSearchResDto;
import com.example.travelshooting.restaurant.entity.Restaurant;
import com.example.travelshooting.restaurant.repository.RestaurantRepository;
import com.example.travelshooting.file.service.S3Service;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    @Value("${gg.api.food.key}")
    private String ggFoodKey;

    @Value("${gg.api.food.url}")
    private String ggFoodUrl;

    @Transactional
    public List<GgRestaurantResDto> saveGgRestaurants(int page, int size) {
        String url = UriComponentsBuilder.fromHttpUrl(ggFoodUrl)
                .queryParam("KEY", ggFoodKey)
                .queryParam("pIndex", page)
                .queryParam("pSize", size)
                .build()
                .toUriString();

        XmlMapper xmlMapper = new XmlMapper();
        List<GgRestaurantApiDto> apiDtos;

        try {
            String xmlData = restTemplate.getForObject(url, String.class);
            apiDtos = xmlMapper.readValue(xmlData, xmlMapper.getTypeFactory().constructCollectionType(List.class, GgRestaurantApiDto.class));

            // 음식점명이 null인 항목 제거
            apiDtos = apiDtos.stream()
                    .filter(apiDto -> apiDto.getPlaceName() != null && !apiDto.getPlaceName().isEmpty())
                    .collect(Collectors.toList());
        } catch (ResourceAccessException e) {
            throw new ResourceAccessException("타임아웃이 발생했습니다.");
        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 오류가 발생했습니다.");
        }

        // 기존에 저장된 데이터 조회 (업소명, 주소, 전화번호 기준으로 중복 여부를 체크)
        List<Restaurant> existingRestaurants = restaurantRepository.findAll();

        // 중복 저장 방지
        List<Restaurant> savedRestaurants = apiDtos.stream()
                .filter(apiDto -> apiDto.getPlaceName() != null && !apiDto.getPlaceName().isEmpty())
                .map(apiDto -> {

                    boolean isExistingRestaurant = existingRestaurants.stream()
                            .anyMatch(existing -> existing.getPlaceName().equals(apiDto.getPlaceName()) &&
                                    existing.getAddressName().equals(apiDto.getAddressName()) &&
                                    existing.getPhone().equals(apiDto.getPhone()));

                    if (!isExistingRestaurant) {
                        Restaurant restaurant = new Restaurant(
                                Const.GG_NAME,
                                apiDto.getCity(),
                                apiDto.getPlaceName(),
                                apiDto.getAddressName(),
                                apiDto.getRoadAddressName(),
                                apiDto.getPhone(),
                                apiDto.getLongitude(),
                                apiDto.getLatitude()
                        );
                        return restaurantRepository.save(restaurant);
                    }
                    return null;  // 중복된 데이터는 null로 처리
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return savedRestaurants.stream()
                .map(restaurant -> new GgRestaurantResDto(
                        restaurant.getId(),
                        restaurant.getRegion(),
                        restaurant.getCity(),
                        restaurant.getPlaceName(),
                        restaurant.getAddressName(),
                        restaurant.getRoadAddressName(),
                        restaurant.getPhone(),
                        restaurant.getLongitude(),
                        restaurant.getLatitude()

                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<RestaurantSearchResDto> findRestaurants(String placeName, String region, String city, Pageable pageable) {
        return restaurantRepository.findRestaurants(placeName, region, city, pageable);
    }

    @Transactional
    public RestaurantResDto uploadFile(Long restaurantId, MultipartFile file) {
        Restaurant restaurant = restaurantRepository.findRestaurantById(restaurantId);

        try {
            String fileUrl = s3Service.uploadFile(file);

            restaurant.updateImage(fileUrl);
            restaurantRepository.save(restaurant);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new RestaurantResDto(
                restaurant.getId(),
                restaurant.getRegion(),
                restaurant.getCity(),
                restaurant.getPlaceName(),
                restaurant.getAddressName(),
                restaurant.getRoadAddressName(),
                restaurant.getPhone(),
                restaurant.getLongitude(),
                restaurant.getLatitude(),
                restaurant.getImageUrl()
        );
    }

    public Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findRestaurantById(restaurantId);
    }
}
