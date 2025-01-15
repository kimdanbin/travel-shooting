package com.example.travelshooting.product.service;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.service.CompanyService;
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

    private final ProductRepository productRepository;
    private final CompanyService companyService;
    private final UserService userService;

    @Transactional
    public CreateProductResDto createProduct(Long companyId, String name, String description, int price, String address, int quantity) {
        Company company = companyService.findCompanyById(companyId);
        User user = userService.findAuthenticatedUser();
        // TODO 상품을 등록하려는 사람이 해당 업체의 소유자인지 확인
        if(!company.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 상품을 등록할 수 있습니다.");
        }
        // TODO 이미 등록된 업체 이름인지 확인
        if (productRepository.existsByCompanyAndName(company, name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "같은 업체에서 동일한 이름의 상품을 중복 등록할 수 없습니다.");
        }

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
//        Page<Product> productPage = productRepository.findAll(pageable);
        Page<Product> productPage;

        // 상품명 검색을 통한 검색 결과 조회
        if (productName != null && !productName.isEmpty()) {
            productPage = productRepository.findByNameContaining(productName, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
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
        User user = userService.findAuthenticatedUser();
        // TODO 상품을 수정하려는 사람이 해당 업체의 소유자인지 확인
        if(!findProduct.getCompany().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 상품을 수정할 수 있습니다.");
        }
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
        User user = userService.findAuthenticatedUser();
        // TODO 상품을 삭제하려는 사람이 해당 업체의 소유자인지 확인
        if(!findProduct.getCompany().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 상품을 삭제할 수 있습니다.");
        }
        productRepository.delete(findProduct);
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + productId + "에 해당하는 레저/티켓 상품을 찾을 수 없습니다."));
    }

}
