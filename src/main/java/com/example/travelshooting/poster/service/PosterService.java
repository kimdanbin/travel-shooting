package com.example.travelshooting.poster.service;

import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.poster.repository.PosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PosterService {
    private final PosterRepository posterRepository;

    public Poster getPosterById(Long posterId) {
        return posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 " + posterId + "에 해당하는 포스터를 찾을 수 없습니다."));
    }
}
