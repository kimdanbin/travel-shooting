package com.example.travelshooting.local.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.local.dto.LocalResDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LocalService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.map.key}")
    private String kakaoAk;

    @Value("${kakao.api.map.url}")
    private String kakaoMapUrl;

    @Transactional(readOnly = true)
    public List<LocalResDto> searchPlaces(String keyword, int page, int size) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoMapUrl)
                .queryParam("query", keyword)
                .queryParam("page", page)
                .queryParam("size", size);

        if (page < Const.LOCAL_PAGE_DEFAULT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지는 1부터 시작합니다.");
        }

        URI uri = uriBuilder.build().encode().toUri();

        // 인증 요청에 필요한 REST API 키를 헤더에 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, Const.KAKAO_MAP_HEADER + " " + kakaoAk);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<LocalResDto> locals = new ArrayList<>();

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

            if (documents.isMissingNode() || !documents.isArray() || documents.size() == 0) {
                return Collections.emptyList();
            }

            // JSON 데이터를 LocalResDto로 변환
            locals = StreamSupport.stream(documents.spliterator(), false)
                    .map(document -> new LocalResDto(
                            document.path("id").asLong(),
                            document.path("category_name").asText(),
                            document.path("place_name").asText(),
                            document.path("address_name").asText(),
                            document.path("road_address_name").asText(),
                            document.path("phone").asText(),
                            document.path("x").asText(),
                            document.path("y").asText()
                    ))
                    .collect(Collectors.toList());
        } catch (ResourceAccessException e) {
            throw new ResourceAccessException("타임아웃이 발생했습니다.");
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류가 발생했습니다.");
        }

        return locals;
    }
}
