package com.example.travelshooting.part.repository;

import com.example.travelshooting.company.entity.QCompany;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.entity.QPart;
import com.example.travelshooting.product.entity.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartCustomRepositoryImpl implements PartCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Part findPartByProductIdAndUserIdAndId(Long productId, Long userId, Long partId) {

        QPart part = QPart.part;
        QProduct product = QProduct.product;
        QCompany company = QCompany.company;

        return jpaQueryFactory
                .selectFrom(part)
                .innerJoin(part.product, product)
                .innerJoin(product.company, company)
                .where(
                        product.id.eq(productId),
                        company.user.id.eq(userId),
                        part.id.eq(partId)
                )
                .fetchOne();
    }
}
