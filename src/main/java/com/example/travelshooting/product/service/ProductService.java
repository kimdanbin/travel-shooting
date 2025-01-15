package com.example.travelshooting.product.service;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.service.CompanyService;
import com.example.travelshooting.product.dto.*;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

//    private final PartService partService;
    private final ProductRepository productRepository;
    private final CompanyService companyService;

    @Transactional
    public CreateProductResDto createProduct(Long companyId, String name, String description, int price, String address, int quantity) {
        Company company = companyService.findCompanyById(companyId);
        Product product = new Product(name, description, price, address, quantity, company);
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

    @Transactional(readOnly = true)
    public List<ProductResDto> findAllProducts(int page, int size, String productName) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        // 상품명 검색을 통한 검색 결과 조회
        if (productName != null && !productName.isEmpty()) {
            productPage = productRepository.findByNameContaining(productName, pageable);
        }

        return productPage.getContent().stream()
                .map(product -> new ProductResDto(
                        product.getId(),
                        product.getCompany().getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getAddress(),
                        product.getQuantity(),
                        product.getCreatedAt(),
                        product.getUpdatedAt()
                ))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public ProductDetailResDto findProduct(Long productId) {
        Product findProduct = productRepository.findByIdOrElseThrow(productId);

        return ProductDetailResDto.toDto(findProduct);
    }


    @Transactional
    public UpdateProductResDto updateProduct(Long productId, String description, int price, String address, int quantity) {
        Product findProduct = productRepository.findByIdOrElseThrow(productId);
        findProduct.updateProduct(description, price, address, quantity);
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

    @Transactional
    public void deleteCompany(Long productId) {
        Product findProduct = productRepository.findByIdOrElseThrow(productId);
        productRepository.delete(findProduct);
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + productId + "에 해당하는 레저/티켓 상품을 찾을 수 없습니다."));
    }

}
