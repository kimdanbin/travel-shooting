package com.example.travelshooting.product.service;

import com.example.travelshooting.company.Company;
import com.example.travelshooting.company.service.CompanyService;
import com.example.travelshooting.product.Product;
import com.example.travelshooting.product.dto.CreateProductReqDto;
import com.example.travelshooting.product.dto.CreateProductResDto;
import com.example.travelshooting.product.dto.UpdateProductReqDto;
import com.example.travelshooting.product.dto.UpdateProductResDto;
import com.example.travelshooting.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyService companyService;

    @Transactional
    public CreateProductResDto createProduct(Long companyId, CreateProductReqDto createProductReqDto) {

        Company company = companyService.getCompanyById(companyId);
        Product product = createProductReqDto.toEntity(company);
        productRepository.save(product);

        return new CreateProductResDto(
                product.getId(),
                product.getCompany().getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getAddress(),
                product.getQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Transactional
    public UpdateProductResDto updateProduct(Long productId, UpdateProductReqDto updateProductReqDto) {
        Product findProduct = productRepository.findByIdOrElseThrow(productId);
        findProduct.updateProduct(
                updateProductReqDto.getDescription(),
                updateProductReqDto.getPrice(),
                updateProductReqDto.getAddress(),
                updateProductReqDto.getQuantity()
        );
        productRepository.save(findProduct);

        return new UpdateProductResDto(
                findProduct.getId(),
                findProduct.getCompany().getId(),
                findProduct.getName(),
                findProduct.getDescription(),
                findProduct.getPrice(),
                findProduct.getAddress(),
                findProduct.getQuantity(),
                findProduct.getCreatedAt(),
                findProduct.getUpdatedAt()
        );
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 " + productId + "에 해당하는 레저/티켓 상품을 찾을 수 없습니다."));
    }
}
