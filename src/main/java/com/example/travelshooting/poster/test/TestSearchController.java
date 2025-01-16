package com.example.travelshooting.poster.test;

import com.example.travelshooting.common.CommonListResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posters")
@RequiredArgsConstructor
public class TestSearchController {

    private final TestSearchService testSearchService;

    @GetMapping("/search")
    public ResponseEntity<CommonListResDto<TestSearchResDto>> findAll(@RequestParam(required = false) Integer minExpenses,
                                                                      @RequestParam(required = false) Integer maxExpenses,
                                                                      Pageable pageable) {
        Page<TestSearchResDto> posters = testSearchService.findAll(minExpenses, maxExpenses, pageable);
        List<TestSearchResDto> content = posters.getContent();

        return new ResponseEntity<>(new CommonListResDto<>("여행 코스 검색 완료", content), HttpStatus.OK);
    }
}
