package com.example.travelshooting.like.controller;

import com.example.travelshooting.like.service.LikePosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikePosterController {
    private final LikePosterService likePosterService;

    // 포스터에 좋아요 누르기
    @PostMapping("/posters/{posterId}/likes")
    public ResponseEntity<String> likePoster(
            @PathVariable Long posterId
    ) {

        likePosterService.likePoster(posterId);

        return new ResponseEntity<>("좋아요를 눌렀습니다.", HttpStatus.CREATED);
    }

    // 좋아요 누른 포스터에 좋아요 취소하기
    @DeleteMapping("/posters/{posterId}/likes")
    public ResponseEntity<String> unlikePoster(
            @PathVariable Long posterId
    ) {

        likePosterService.unlikePoster(posterId);

        return new ResponseEntity<>("좋아요를 취소 했습니다.", HttpStatus.CREATED);
    }
}
