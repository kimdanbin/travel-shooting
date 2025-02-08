package com.example.travelshooting.payment.repository;

import com.example.travelshooting.part.entity.QPart;
import com.example.travelshooting.payment.dto.PaymentResDto;
import com.example.travelshooting.payment.dto.QPaymentResDto;
import com.example.travelshooting.payment.entity.Payment;
import com.example.travelshooting.payment.entity.QPayment;
import com.example.travelshooting.product.entity.QProduct;
import com.example.travelshooting.reservation.entity.QReservation;
import com.example.travelshooting.user.entity.QUser;
import com.example.travelshooting.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PaymentResDto> findPayments(User authenticatedUser, Pageable pageable) {
        QPayment payment = QPayment.payment;
        QProduct product = QProduct.product;
        QPart part = QPart.part;
        QUser user = QUser.user;
        QReservation reservation = QReservation.reservation;

        QueryResults<PaymentResDto> queryResults = jpaQueryFactory
                .select(new QPaymentResDto(
                        payment.id,
                        reservation.id,
                        product.name,
                        reservation.headCount,
                        payment.totalPrice,
                        payment.type,
                        payment.status,
                        payment.refundType,
                        payment.cancelPrice,
                        payment.createdAt,
                        payment.updatedAt))
                .from(payment)
                .innerJoin(reservation).on(payment.reservation.id.eq(reservation.id)).fetchJoin()
                .innerJoin(user).on(reservation.user.id.eq(user.id)).fetchJoin()
                .innerJoin(part).on(reservation.part.id.eq(part.id)).fetchJoin()
                .innerJoin(product).on(part.product.id.eq(product.id)).fetchJoin()
                .where(user.id.eq(authenticatedUser.getId()))
                .orderBy(payment.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    public PaymentResDto findPaymentByIdAndUserId(Payment existingPayment, User authenticatedUser) {
        QPayment payment = QPayment.payment;
        QProduct product = QProduct.product;
        QPart part = QPart.part;
        QUser user = QUser.user;
        QReservation reservation = QReservation.reservation;

        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(payment.id.eq(existingPayment.getId()));
        conditions.and(user.id.eq(authenticatedUser.getId()));

        return jpaQueryFactory
                .select(new QPaymentResDto(
                        payment.id,
                        reservation.id,
                        product.name,
                        reservation.headCount,
                        payment.totalPrice,
                        payment.type,
                        payment.status,
                        payment.refundType,
                        payment.cancelPrice,
                        payment.createdAt,
                        payment.updatedAt))
                .from(payment)
                .innerJoin(reservation).on(payment.reservation.id.eq(reservation.id)).fetchJoin()
                .innerJoin(user).on(reservation.user.id.eq(user.id)).fetchJoin()
                .innerJoin(part).on(reservation.part.id.eq(part.id)).fetchJoin()
                .innerJoin(product).on(part.product.id.eq(product.id)).fetchJoin()
                .where(conditions)
                .fetchOne();
    }
}
