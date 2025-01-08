package com.example.travelshooting.product.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.product.dto.CreateProductReqDto;
import com.example.travelshooting.product.dto.CreateProductResDto;
import com.example.travelshooting.product.service.ProductService;
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
public class ProductController {

    private final ProductService productService;

    /**
     * 레저/티켓 상품 생성 API
     *
     * @param createProductReqDto 생성할 레저/티켓 상품의 정보를 담고 있는 dto
     * @return 생성된 레저/티켓 상품의 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @PostMapping("/partners/companies/{companyId}/leisures")
    public ResponseEntity<CommonResDto<CreateProductResDto>> CreateProduct(
            @PathVariable Long companyId,
            @Valid @RequestBody CreateProductReqDto createProductReqDto
    ) {
        CreateProductResDto result = productService.createProduct(companyId, createProductReqDto);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 생성 완료", result), HttpStatus.CREATED);
    }

}
