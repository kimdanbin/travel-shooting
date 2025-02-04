package com.example.travelshooting.reservation.repository;

import com.example.travelshooting.company.entity.QCompany;
import com.example.travelshooting.part.entity.QPart;
import com.example.travelshooting.product.entity.QProduct;
import com.example.travelshooting.reservation.dto.QReservationResDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
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

import java.time.LocalDate;

@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ReservationResDto> findAllByUserIdAndProductId(Long productId, User authenticatedUser, Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(product.id.eq(productId));
        conditions.and(user.id.eq(authenticatedUser.getId()));

        QueryResults<ReservationResDto> queryResults = jpaQueryFactory
                .select(new QReservationResDto(
                        reservation.id,
                        user.id,
                        product.id,
                        part.id,
                        reservation.reservationDate,
                        reservation.headCount,
                        reservation.totalPrice,
                        reservation.status,
                        reservation.createdAt,
                        reservation.updatedAt))
                .from(reservation)
                .innerJoin(user).on(reservation.user.id.eq(user.id)).fetchJoin()
                .innerJoin(part).on(reservation.part.id.eq(part.id)).fetchJoin()
                .innerJoin(product).on(part.product.id.eq(product.id)).fetchJoin()
                .where(conditions)
                .orderBy(reservation.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    public ReservationResDto findReservationByProductIdAndId(Long productId, Long reservationId, Long userId) {
        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(product.id.eq(productId));
        conditions.and(user.id.eq(userId));
        conditions.and(reservation.id.eq(reservationId));

        return jpaQueryFactory
                .select(new QReservationResDto(
                        reservation.id,
                        user.id,
                        product.id,
                        part.id,
                        reservation.reservationDate,
                        reservation.headCount,
                        reservation.totalPrice,
                        reservation.status,
                        reservation.createdAt,
                        reservation.updatedAt))
                .from(reservation)
                .innerJoin(user).on(reservation.user.id.eq(user.id)).fetchJoin()
                .innerJoin(part).on(reservation.part.id.eq(part.id)).fetchJoin()
                .innerJoin(product).on(part.product.id.eq(product.id)).fetchJoin()
                .where(conditions)
                .fetchOne();
    }

    @Override
    public Page<ReservationResDto> findPartnerReservationsByProductIdAndUserId(Long productId, User authenticatedUser, Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QCompany company = QCompany.company;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(product.id.eq(productId));
        conditions.and(company.user.id.eq(authenticatedUser.getId()));

        QueryResults<ReservationResDto> queryResults = jpaQueryFactory
                .select(new QReservationResDto(
                        reservation.id,
                        reservation.user.id,
                        product.id,
                        part.id,
                        reservation.reservationDate,
                        reservation.headCount,
                        reservation.totalPrice,
                        reservation.status,
                        reservation.createdAt,
                        reservation.updatedAt))
                .from(reservation)
                .innerJoin(part).on(reservation.part.id.eq(part.id)).fetchJoin()
                .innerJoin(product).on(part.product.id.eq(product.id)).fetchJoin()
                .innerJoin(company).on(product.company.id.eq(company.id)).fetchJoin()
                .where(conditions)
                .orderBy(reservation.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    public ReservationResDto findPartnerReservationByProductIdAndId(Long productId, Long reservationId, User authenticatedUser) {
        QReservation reservation = QReservation.reservation;
        QCompany company = QCompany.company;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(product.id.eq(productId));
        conditions.and(company.user.id.eq(authenticatedUser.getId()));
        conditions.and(reservation.id.eq(reservationId));

        return jpaQueryFactory
                .select(new QReservationResDto(
                        reservation.id,
                        reservation.user.id,
                        product.id,
                        part.id,
                        reservation.reservationDate,
                        reservation.headCount,
                        reservation.totalPrice,
                        reservation.status,
                        reservation.createdAt,
                        reservation.updatedAt))
                .from(reservation)
                .innerJoin(part).on(reservation.part.id.eq(part.id)).fetchJoin()
                .innerJoin(product).on(part.product.id.eq(product.id)).fetchJoin()
                .innerJoin(company).on(product.company.id.eq(company.id)).fetchJoin()
                .where(conditions)
                .fetchOne();
    }

    @Override
    public Integer findTotalHeadCountByPartIdAndReservationDate(Long partId, LocalDate reservationDate) {
        QReservation reservation = QReservation.reservation;

        BooleanBuilder conditions = new BooleanBuilder();

        conditions.and(reservation.part.id.eq(partId));
        conditions.and(reservation.reservationDate.eq(reservationDate));

        return jpaQueryFactory
                .select(reservation.headCount.sum().coalesce(0))
                .from(reservation)
                .where(conditions)
                .fetchOne();
    }
}
