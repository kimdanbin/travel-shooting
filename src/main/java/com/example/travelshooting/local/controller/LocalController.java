package com.example.travelshooting.local.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.local.dto.LocalResDto;
import com.example.travelshooting.local.service.LocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    @GetMapping("/locals")
    public ResponseEntity<CommonListResDto<LocalResDto>> searchPlaces(@RequestParam(required = false) String keyword,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam int size) {
        List<LocalResDto> local = localService.searchPlaces(keyword, page, size);

        return new ResponseEntity<>(new CommonListResDto<>("장소 검색 완료", local), HttpStatus.OK);
    }
}
