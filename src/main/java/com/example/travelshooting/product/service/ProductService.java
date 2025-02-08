package com.example.travelshooting.product.service;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.service.CompanyService;
import com.example.travelshooting.part.dto.PartResDto;
import com.example.travelshooting.product.dto.CreateProductResDto;
import com.example.travelshooting.product.dto.ProductDetailResDto;
import com.example.travelshooting.product.dto.ProductResDto;
import com.example.travelshooting.product.dto.UpdateProductResDto;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.repository.ProductRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyService companyService;
    private final UserService userService;

    @Transactional
    public CreateProductResDto createProduct(Long companyId, String name, String description, Integer price, String address, LocalDate saleStartAt, LocalDate saleEndAt) {
        User user = userService.findAuthenticatedUser();
        Company company = companyService.findCompanyByIdAndUserId(companyId, user.getId());
        if (company == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 상품을 등록할 수 있습니다.");
        }

        // 이미 등록된 업체 이름인지 확인
        if (productRepository.existsByCompanyAndName(company, name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "같은 업체에서 동일한 이름의 상품을 중복 등록할 수 없습니다.");
        }

        Product product = new Product(name, description, price, address, saleStartAt, saleEndAt, company);
        productRepository.save(product);

        return new CreateProductResDto(
                product.getId(),
                product.getCompany().getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getAddress(),
                product.getSaleStartAt(),
                product.getSaleEndAt(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResDto> findAllProducts(Pageable pageable, String productName) {
        return productRepository.findAllProducts(pageable, productName);
    }

    @Transactional(readOnly = true)
    public ProductDetailResDto findProduct(Long companyId, Long productId) {
        Product findProduct = productRepository.findByCompanyIdAndId(companyId, productId);
        if (findProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 업체가 가진 해당 ID의 상품이 없습니다.");
        }

        List<PartResDto> parts = findProduct.getParts().stream()
                .map(part -> new PartResDto(part.getId(), part.getOpenAt(), part.getCloseAt(), part.getMaxQuantity()))
                .collect(Collectors.toList());

        return new ProductDetailResDto(
                findProduct.getId(),
                findProduct.getCompany().getId(),
                findProduct.getName(),
                findProduct.getDescription(),
                findProduct.getPrice(),
                findProduct.getAddress(),
                findProduct.getSaleStartAt(),
                findProduct.getSaleEndAt(),
                findProduct.getCreatedAt(),
                findProduct.getUpdatedAt(),
                parts
        );
    }

    @Transactional
    public UpdateProductResDto updateProduct(Long companyId, Long productId, String description, Integer price, String address, LocalDate saleStartAt, LocalDate saleEndAt) {
        Product findProduct = productRepository.findByCompanyIdAndId(companyId, productId);
        if (findProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 업체가 가진 해당 ID의 상품이 없습니다.");
        }

        User user = userService.findAuthenticatedUser();
        Product product = productRepository.findProductByIdAndUserId(findProduct.getId(), user.getId());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 상품을 수정할 수 있습니다.");
        }

        findProduct.updateProduct(description, price, address, saleStartAt, saleEndAt);
        productRepository.save(findProduct);

        return new UpdateProductResDto(
                findProduct.getId(),
                findProduct.getCompany().getId(),
                findProduct.getName(),
                findProduct.getDescription(),
                findProduct.getPrice(),
                findProduct.getAddress(),
                findProduct.getSaleStartAt(),
                findProduct.getSaleEndAt(),
                findProduct.getCreatedAt(),
                findProduct.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteProduct(Long companyId, Long productId) {
        Product findProduct = productRepository.findByCompanyIdAndId(companyId, productId);
        if (findProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 업체가 가진 해당 ID의 상품이 없습니다.");
        }

        User user = userService.findAuthenticatedUser();
        Product product = productRepository.findProductByIdAndUserId(findProduct.getId(), user.getId());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 상품을 삭제할 수 있습니다.");
        }

        // 해당 상품에 일정이 존재하면 삭제할 수 없음
        Product reservation = productRepository.findPartById(productId);
        if(reservation != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 일정이 존재하여 삭제할 수 없습니다.");
        }

        productRepository.delete(findProduct);
    }

    public Product findProductById(Long productId) {
        return productRepository.findProductById(productId);
    }

    public Product findProductByProductIdAndUserId(Long productId, Long userId) {
        return productRepository.findProductByIdAndUserId(productId, userId);
    }
}
