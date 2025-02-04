package com.example.travelshooting.product.repository;

import com.example.travelshooting.product.dto.ProductResDto;
import com.example.travelshooting.product.dto.QProductResDto;
import com.example.travelshooting.product.entity.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProductResDto> findAllProducts(Pageable pageable, String productName) {

        QProduct product = QProduct.product;

        BooleanBuilder conditions = new BooleanBuilder();

        if (productName != null && !productName.isEmpty()) {
            conditions.and(product.name.containsIgnoreCase(productName));
        }

        QueryResults<ProductResDto> queryResults = jpaQueryFactory
                .select(new QProductResDto(
                        product.id,
                        product.company.id,
                        product.name,
                        product.description,
                        product.price,
                        product.address,
                        product.saleStartAt,
                        product.saleEndAt,
                        product.createdAt,
                        product.updatedAt
                ))
                .from(product)
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
