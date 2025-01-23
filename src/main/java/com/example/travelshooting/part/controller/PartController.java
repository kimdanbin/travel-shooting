package com.example.travelshooting.part.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.part.dto.CreatePartReqDto;
import com.example.travelshooting.part.dto.PartResDto;
import com.example.travelshooting.part.dto.UpdatePartReqDto;
import com.example.travelshooting.part.service.PartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partners/products/{productId}/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    /**
     * 레저/티켓 일정 생성 API
     *
     * @param productId 일정을 생성할 레저/티켓 상품의 id
     * @param createPartReqDto 생성할 일정의 정보를 담고있는 dto
     * @return 생성된 일정의 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @PostMapping
    public ResponseEntity<CommonResDto<PartResDto>> createPart (
            @PathVariable Long productId,
            @Valid @RequestBody CreatePartReqDto createPartReqDto
    ) {
        PartResDto result = partService.createPart(productId, createPartReqDto.getOpenAt(),createPartReqDto.getCloseAt(),createPartReqDto.getMaxQuantity());

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 일정 등록 완료", result), HttpStatus.CREATED);
    }

    /**
     * 레저/티켓 일정 수정 API
     *
     * @param partId 수정할 레저/티켓 일정의 id
     * @param updatePartReqDto 수정할 레저/티켓 일정의 정보를 담고 있는 dto
     * @return 수정된 레저/티켓 일정의 정보를 담고 있는 dto. 성공시 상태코드 200 반환
     */
    @PatchMapping("/{partId}")
    public ResponseEntity<CommonResDto<PartResDto>> updatePart (
            @PathVariable Long productId,
            @PathVariable Long partId,
            @Valid @RequestBody UpdatePartReqDto updatePartReqDto
    ) {
        PartResDto result = partService.updatePart(productId, partId, updatePartReqDto.getOpenAt(),updatePartReqDto.getCloseAt(),updatePartReqDto.getMaxQuantity());

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 일정 수정 완료", result), HttpStatus.OK);
    }

    /**
     * 레저/티켓 일정 삭제 API
     *
     * @param partId 삭제할 레저/티켓 일정의 id
     * @return 삭제 성공 시, 메시지와 함께 상태코드 200 반환
     */
    @DeleteMapping("/{partId}")
    public ResponseEntity<String> deletePart(@PathVariable Long productId, @PathVariable Long partId) {
        partService.deletePart(productId, partId);

        return new ResponseEntity<>("레저/티켓 일정 삭제 완료", HttpStatus.OK);
    }
}