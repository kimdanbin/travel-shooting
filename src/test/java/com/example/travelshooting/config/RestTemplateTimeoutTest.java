package com.example.travelshooting.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestTemplateTimeoutTest {

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        restTemplate = new RestTemplate(factory);
    }

    @Test
    @DisplayName("타임아웃 테스트")
    public void testRestTemplateTimeout() {
        // given
        String url = "http://example-url";

        // when, then
        // 5초를 초과해 ResourceAccessException 예외가 발생하는지 테스트
        assertThrows(ResourceAccessException.class, () -> {
            restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        });
    }
}
