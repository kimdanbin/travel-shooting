package com.example.travelshooting.product.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.product.dto.*;
import com.example.travelshooting.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 레저/티켓 상품 전체 조회 API
     * 페이징 처리 된 상품 목록 조회. 상품명으로 검색할 수 있습니다.
     *
     * @param page        페이지 번호
     * @param size        페이지당 항목 수
     * @param productName 검색할 상품명
     * @return 조회된 레저/티켓 상품의 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @GetMapping("/products")
    public ResponseEntity<CommonListResDto<ProductResDto>> findAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String productName
    ) {
        List<ProductResDto> result = productService.findAllProducts(page, size, productName);

        return new ResponseEntity<>(new CommonListResDto<>("레저/티켓 전체 조회 완료", result), HttpStatus.OK);
    }

    /**
     * 레저/티켓 상품 단 건 조회 API
     * 조회 시 상품별 일정도 함께 조회
     *
     * @param productId 조회할 레저/티켓 상품의 id
     * @return 조회된 레저/티켓 상품의 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @GetMapping("companies/{companyId}/products/{productId}")
    public ResponseEntity<CommonResDto<ProductDetailResDto>> findProduct(@PathVariable Long productId) {
        ProductDetailResDto result = productService.findProduct(productId);

        return new ResponseEntity<>(new CommonResDto<>("레저/티켓 단건 조회 완료", result), HttpStatus.OK);
    }

    /**
     * 레저/티켓 상품 수정 API
     *
     * @param productId           수정할 레저/티켓 상품의 id
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

    /**
     * 레저/티켓 상품 삭제 API
     *
     * @param productId 삭제할 레저/티켓 상품의 id
     * @return 삭제 성공 시, 메시지와 함께 상태코드 200 반환
     */
    @DeleteMapping("/partners/companies/{companyId}/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteCompany(productId);

        return new ResponseEntity<>("레저/티켓 삭제 완료", HttpStatus.OK);
    }

}
