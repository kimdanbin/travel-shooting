package com.example.travelshooting.poster.service;

import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.repository.PosterRepository;
import com.example.travelshooting.restaurant.Restaurant;
import com.example.travelshooting.restaurant.service.RestaurantService;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PosterService {
    private final PosterRepository posterRepository;
    private final UserService userService;
    private final RestaurantService restaurantService;

    // 포스터 생성
    public PosterResDto createPoster(Long restaurantId, Long paymentId, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt) {

        // 나중에 로그인 로직 구현되면 수정
        User user = userService.getUserById(1L);

        Restaurant restaurant;

        if (restaurantId == null) {
            restaurant = null;
        } else {
            restaurant = restaurantService.getRestaurantById(restaurantId);
        }

        // payment 도 나중에 구현되면 추가

        Poster poster = Poster.builder()
//                .user(user)
                .restaurant(restaurant)
                .title(title)
                .content(content)
                .travelStartAt(travelStartAt)
                .travelEndAt(travelEndAt)
                .build();

        return new PosterResDto(posterRepository.save(poster));
    }

    // 포스터 단건 조회
    public PosterResDto findPoster(Long posterId) {

        return new PosterResDto(getPosterById(posterId));
    }

    // 포스터 수정
    public PosterResDto updatePoster(Long posterId, Long restaurantId, Long paymentId, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt) {

        // 나중에 로그인 로직 구현되면 수정
        User user = userService.getUserById(1L);

        Poster poster = getPosterById(posterId);

        Restaurant restaurant;

        if (restaurantId == null) {
            restaurant = null;
        } else {
            restaurant = restaurantService.getRestaurantById(restaurantId);
        }

        // payment 도 나중에 구현되면 추가

        poster.updateRestaurant(restaurant);
        poster.updateTitle(title);
        poster.updateContent(content);
        poster.updateTravelStartAt(travelStartAt);
        poster.updateTravelEndAt(travelEndAt);

        return new PosterResDto(posterRepository.save(poster));
    }

    // 포스터 삭제
    public void deletePoster(Long posterId) {

        posterRepository.deleteById(posterId);
    }

    // 포스터 아이디로 포스터 찾기
    public Poster getPosterById(Long posterId) {
        return posterRepository.findById(posterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + posterId + "에 해당하는 포스터를 찾을 수 없습니다."));
    }
}
