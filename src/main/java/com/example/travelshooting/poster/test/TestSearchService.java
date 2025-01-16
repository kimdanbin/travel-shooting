package com.example.travelshooting.poster.test;

import com.example.travelshooting.like.entity.QLikePoster;
import com.example.travelshooting.poster.entity.QPoster;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestSearchService {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<TestSearchResDto> findAll(Integer minExpenses, Integer maxExpenses, Pageable pageable) {
        QPoster poster = QPoster.poster;
        QLikePoster likePoster = QLikePoster.likePoster;

        BooleanBuilder conditions = new BooleanBuilder();

        if (minExpenses != null) {
            conditions.or(poster.expenses.goe(minExpenses)); // goe : >= min보다 크거나 같고
        }

        if (maxExpenses != null) {
            conditions.or(poster.expenses.loe(maxExpenses)); // loe : <= max보다 작거나 같음
        }

        QueryResults<TestSearchResDto> queryResults = jpaQueryFactory
                .select(new QTestSearchResDto(
                        poster.id,
                        poster.title,
                        poster.expenses,
                        poster.travelStartAt,
                        poster.travelEndAt,
                        poster.likePosters.size()))
                .from(poster)
                .innerJoin(likePoster).on(poster.id.eq(likePoster.poster.id))
                .where(conditions)
                .groupBy(poster.id, poster.title, poster.expenses, poster.travelStartAt, poster.travelEndAt)
                .orderBy(likePoster.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
