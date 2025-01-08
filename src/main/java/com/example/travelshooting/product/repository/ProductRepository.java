package com.example.travelshooting.product.repository;

import com.example.travelshooting.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product findByIdOrElseThrow(Long productId) {
        return findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "레저/티켓 상품이 존재하지 않습니다."));
    }
}
