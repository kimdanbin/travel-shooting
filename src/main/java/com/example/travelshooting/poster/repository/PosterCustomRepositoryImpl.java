package com.example.travelshooting.poster.repository;

import com.example.travelshooting.like.entity.QLikePoster;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.dto.QPosterResDto;
import com.example.travelshooting.poster.entity.QPoster;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
public class PosterCustomRepositoryImpl implements PosterCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PosterResDto> findPosters(Integer minExpenses, Integer maxExpenses, LocalDate travelStartAt, LocalDate travelEndAt, Integer days, Integer month, Pageable pageable) {
        QPoster poster = QPoster.poster;
        QLikePoster likePoster = QLikePoster.likePoster;

        BooleanBuilder conditions = new BooleanBuilder();

        if (minExpenses != null) {
            conditions.and(poster.expenses.goe(minExpenses)); // goe >= 최소값
        }

        if (maxExpenses != null) {
            conditions.and(poster.expenses.loe(maxExpenses)); // loe <= 최대값
        }

        if (travelStartAt != null) {
            conditions.and(poster.travelStartAt.goe(travelStartAt.atStartOfDay()));
        }

        if (travelEndAt != null) {
            conditions.and(poster.travelEndAt.loe(travelEndAt.atTime(LocalTime.MAX)));
        }
        // 사용자가 입력한 month = ? 값과 여행 날짜의 월만 추출 후 비교
        if (month != null) {
            conditions.and(poster.travelStartAt.month().eq(month));
        }
        if (days != null) {
            conditions.and(
                    Expressions.numberTemplate(Long.class, "DATEDIFF({0}, {1})",
                            poster.travelEndAt, poster.travelStartAt
                    ).eq((long) days - 1) // 5일이면 DATEDIFF는 4로 계산됨 (종료일 - 시작일)
            );
        }

        QueryResults<PosterResDto> queryResults = jpaQueryFactory
                .select(new QPosterResDto(
                        poster.id,
                        poster.user.id,
                        poster.restaurant.id,
                        poster.payment.id,
                        poster.expenses,
                        poster.title,
                        poster.content,
                        poster.travelStartAt,
                        poster.travelEndAt,
                        poster.likePosters.size(),
                        poster.createdAt,
                        poster.updatedAt))
                .from(poster)
                .leftJoin(likePoster).on(poster.id.eq(likePoster.poster.id))
                .where(conditions)
                .groupBy(
                        poster.id,
                        poster.user.id,
                        poster.restaurant.id,
                        poster.payment.id,
                        poster.expenses,
                        poster.title,
                        poster.content,
                        poster.travelStartAt,
                        poster.travelEndAt,
                        poster.createdAt,
                        poster.updatedAt
                )
                .orderBy(likePoster.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
