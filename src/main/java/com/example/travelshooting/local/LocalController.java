package com.example.travelshooting.local;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    @GetMapping("/locals")
    public ResponseEntity<Page<LocalResDto>> searchPlaces(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(localService.searchPlaces(keyword, pageable));
    }
}
