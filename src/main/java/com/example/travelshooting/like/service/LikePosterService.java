package com.example.travelshooting.like.service;

import com.example.travelshooting.like.entity.LikePoster;
import com.example.travelshooting.like.repository.LikePosterRepository;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikePosterService {

    private final LikePosterRepository likePosterRepository;
    private final UserService userService;
    private final PosterService posterService;

    // 포스터에 좋아요 누르기
    @Transactional
    public void likePoster(Long posterId) {

        User user = userService.findAuthenticatedUser();
        Poster poster = posterService.findPosterById(posterId);

        // 본인이 작성한 포스터라면 예외
        if (user.getId().equals(poster.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인의 포스터에는 좋아요를 하실 수 없습니다.");
        }

        boolean isLiked = likePosterRepository.existsByUserIdAndPosterId(user.getId(), poster.getId());

        // 이미 좋아요를 했다면 예외
        if (isLiked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 좋아요를 하였습니다.");
        }

        LikePoster likePoster = new LikePoster(user, poster);

        likePosterRepository.save(likePoster);
    }

    // 좋아요 누른 포스터에 좋아요 취소하기
    @Transactional
    public void unlikePoster(Long posterId) {

        User user = userService.findAuthenticatedUser();
        Poster poster = posterService.findPosterById(posterId);

        boolean isLiked = likePosterRepository.existsByUserIdAndPosterId(user.getId(), poster.getId());

        // 좋아요를 하지 않았다면 예외
        if (!isLiked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "좋아요가 이미 취소되어 있습니다.");
        }

        LikePoster liked = likePosterRepository.findByUserIdAndPosterId(user.getId(), poster.getId());

        likePosterRepository.deleteById(liked.getId());
    }

    // 본인이 좋아요 한 포스터만 전체 조회
    @Transactional(readOnly = true)
    public List<PosterResDto> findAllByLikedPoster() {

        User user = userService.findAuthenticatedUser();

        List<LikePoster> allLiked = likePosterRepository.findAllByUserId(user.getId());
        List<PosterResDto> likedPosters = new ArrayList<>();

        for (LikePoster likedPoster : allLiked) {
            Poster poster = posterService.findPosterById(likedPoster.getPoster().getId());
            PosterResDto posterResDto = new PosterResDto(poster);
            likedPosters.add(posterResDto);
        }

        return likedPosters;
    }
}
