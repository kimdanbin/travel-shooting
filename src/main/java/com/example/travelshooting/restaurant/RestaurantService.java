package com.example.travelshooting.restaurant;

import com.example.travelshooting.common.Const;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Value("${kakao.api.map.key}")
    private String apiKey;

    private static final String KAKAO_CATEGORY_API_URL = Const.KAKAO_CATEGORY_API_URL;
    private static final String KAKAO_CATEGORY_CODE_FOOD = Const.KAKAO_CATEGORY_CODE_FOOD;

    public List<RestaurantResDto> saveRestaurants() {
        String url = buildApiUrl(KAKAO_CATEGORY_CODE_FOOD);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        // JSON → KakaoInfoDto 변환
        List<KakaoInfoDto> kakaoInfos = parseKakaoInfo(response.getBody());

        // KakaoInfoDto → Restaurant Entity 변환
        List<Restaurant> restaurants = kakaoInfos.stream()
                .map(this::convertToRestaurant)
                .collect(Collectors.toList());

        restaurantRepository.saveAll(restaurants);

        List<RestaurantResDto> restaurantResDtos = restaurants.stream()
                .map(RestaurantResDto::toDto)
                .collect(Collectors.toList());

        return restaurantResDtos;
    }

    private String buildApiUrl(String categoryCode) {
        return KAKAO_CATEGORY_API_URL + "?category_group_code=" + categoryCode;
    }

    private List<KakaoInfoDto> parseKakaoInfo(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode documents = rootNode.get("documents");

            return objectMapper.readValue(
                    documents.toString(),
                    new TypeReference<List<KakaoInfoDto>>() {}
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 파싱 오류가 발생했습니다.");
        }
    }

    private Restaurant convertToRestaurant(KakaoInfoDto kakaoInfoDTO) {
        return new Restaurant(
                kakaoInfoDTO.getPlace_name(),
                kakaoInfoDTO.getAddress_name(),
                kakaoInfoDTO.getRoad_address_name(),
                kakaoInfoDTO.getPhone(),
                kakaoInfoDTO.getX(),
                kakaoInfoDTO.getY()
        );
    }
}
