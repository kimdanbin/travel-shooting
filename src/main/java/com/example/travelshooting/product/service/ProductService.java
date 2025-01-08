package com.example.travelshooting.product.service;

import com.example.travelshooting.company.Company;
import com.example.travelshooting.company.service.CompanyService;
import com.example.travelshooting.product.Product;
import com.example.travelshooting.product.dto.CreateProductReqDto;
import com.example.travelshooting.product.dto.CreateProductResDto;
import com.example.travelshooting.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyService companyService;

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
}
