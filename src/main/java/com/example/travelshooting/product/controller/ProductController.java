package com.example.travelshooting.product.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.product.dto.CreateProductReqDto;
import com.example.travelshooting.product.dto.CreateProductResDto;
import com.example.travelshooting.product.dto.UpdateProductReqDto;
import com.example.travelshooting.product.dto.UpdateProductResDto;
import com.example.travelshooting.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/partners/companies/{companyId}/products")
    public ResponseEntity<CommonResDto<CreateProductResDto>> createProduct(
            @PathVariable Long companyId,
            @Valid @RequestBody CreateProductReqDto createProductReqDto
    ) {
        CreateProductResDto result = productService.createProduct(companyId, createProductReqDto);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 생성 완료", result), HttpStatus.CREATED);
    }

    /**
     * 레저/티켓 상품 수정 API
     *
     * @param productId 수정할 레저/티켓 상품의 id
     * @param updateProductReqDto 수정할 레저/티켓 상품의 정보를 담고 있는 dto
     * @return 수정된 레저/티켓 상품의 정보를 담고 있는 dto. 성공시 상태코드 200 반환
     */
    @PatchMapping("/partners/companies/{companyId}/products/{productId}")
    public ResponseEntity<CommonResDto<UpdateProductResDto>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductReqDto updateProductReqDto
    ) {
        UpdateProductResDto result = productService.updateProduct(productId, updateProductReqDto);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 수정 완료", result), HttpStatus.OK);
    }

}
