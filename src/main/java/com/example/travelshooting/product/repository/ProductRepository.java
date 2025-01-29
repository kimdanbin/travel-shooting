package com.example.travelshooting.product.repository;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    default Product findProductById(Long productId) {
        return findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + productId + "에 해당하는 레저/티켓 상품을 찾을 수 없습니다."));
    }

    Page<Product> findByNameContaining(String productName, Pageable pageable);

    boolean existsByCompanyAndName(Company company, String name);

    Product findByCompanyIdAndId(Long companyId, Long id);

    @Query("SELECT p " +
            "FROM Product p " +
            "INNER JOIN p.company c " +
            "WHERE p.id = :productId AND c.user.id = :userId")
    Product findProductByIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);

    @Query("SELECT p FROM Product p JOIN FETCH p.parts pp WHERE p.id = :productId ")
    Product findPartById(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p JOIN FETCH p.parts pp WHERE pp.id = :partId")
    Product findProductByPartId(@Param("partId") Long partId);
}
