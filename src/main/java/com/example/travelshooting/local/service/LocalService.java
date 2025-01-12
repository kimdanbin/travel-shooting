package com.example.travelshooting.local.service;

import com.example.travelshooting.local.dto.LocalResDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LocalService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.map.key}")
    private String apiKey;

    @Value("${kakao.api.map.url}")
    private String KAKAO_API_URL;

    @Transactional(readOnly = true)
    public Page<LocalResDto> searchPlaces(String keyword, Pageable pageable) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL)
                .queryParam("query", keyword);

        URI uri = uriBuilder.build().encode().toUri();

        // 인증 요청에 필요한 REST API 키를 헤더에 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        long totalElements = 0;
        List<LocalResDto> localResDto = new ArrayList<>();

        // API 호출
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // JSON 응답 데이터 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");
            totalElements = root.path("meta").path("total_count").asLong();

            // JSON 데이터를 LocalResDto로 변환
            localResDto = StreamSupport.stream(documents.spliterator(), false)
                    .map(document -> LocalResDto.builder()
                            .id(document.path("id").asLong())
                            .categoryName(document.path("category_name").asText())
                            .placeName(document.path("place_name").asText())
                            .addressName(document.path("address_name").asText())
                            .roadAddressName(document.path("road_address_name").asText())
                            .phone(document.path("phone").asText())
                            .longitude(document.path("x").asText())
                            .latitude(document.path("y").asText())
                            .build())
                    .collect(Collectors.toList());
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new ResourceAccessException("타임아웃이 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 파싱 오류가 발생했습니다.");
        }

        return new PageImpl<>(localResDto, pageable, totalElements);
    }
}
