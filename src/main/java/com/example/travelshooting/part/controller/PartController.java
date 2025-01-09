package com.example.travelshooting.part.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.part.dto.CreatePartReqDto;
import com.example.travelshooting.part.dto.PartResDto;
import com.example.travelshooting.part.service.PartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class PartController {

    private final PartService partService;

    /**
     * 레저/티켓 일정 생성 API
     *
     * @param productId 일정을 생성할 레저/티켓 상품의 id
     * @param createPartReqDto 생성할 일정의 정보를 담고있는 dto
     * @return 생성된 일정의 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @PostMapping("/partners/products/{productId}/parts")
    public ResponseEntity<CommonResDto<PartResDto>> createPart (
            @PathVariable Long productId,
            @Valid @RequestBody CreatePartReqDto createPartReqDto
    ) {
        PartResDto result = partService.createPart(productId, createPartReqDto);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 일정 등록 완료", result), HttpStatus.CREATED);
    }

}