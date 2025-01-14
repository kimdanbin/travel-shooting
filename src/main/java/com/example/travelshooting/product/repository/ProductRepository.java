package com.example.travelshooting.product.repository;

import com.example.travelshooting.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product findByIdOrElseThrow(Long productId) {
        return findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + productId + "에 해당하는 레저/티켓 상품을 찾을 수 없습니다."));
    }

    Page<Product> findByNameContaining(String productName, Pageable pageable);
}
