package com.example.travelshooting.restaurant.repository;

import com.example.travelshooting.poster.entity.QPoster;
import com.example.travelshooting.restaurant.dto.QRestaurantSearchResDto;
import com.example.travelshooting.restaurant.dto.RestaurantSearchResDto;
import com.example.travelshooting.restaurant.entity.QRestaurant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class RestaurantCustomRepositoryImpl implements RestaurantCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<RestaurantSearchResDto> findRestaurants(String placeName, String region, String city, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        QPoster poster = QPoster.poster;

        BooleanBuilder conditions = new BooleanBuilder();

        if (placeName != null) {
            conditions.and(restaurant.placeName.containsIgnoreCase(placeName));
        }

        if (region != null) {
            conditions.and(restaurant.region.containsIgnoreCase(region));
        }

        if (city != null) {
            conditions.and(restaurant.city.containsIgnoreCase(city));
        }

        QueryResults<RestaurantSearchResDto> queryResults = jpaQueryFactory
                .select(new QRestaurantSearchResDto(
                        restaurant.id,
                        restaurant.region,
                        restaurant.city,
                        restaurant.placeName,
                        restaurant.addressName,
                        restaurant.roadAddressName,
                        restaurant.phone,
                        restaurant.longitude,
                        restaurant.latitude,
                        restaurant.imageUrl,
                        restaurant.count().intValue()))
                .from(restaurant)
                .innerJoin(poster).on(restaurant.id.eq(poster.restaurant.id)).fetchJoin()
                .where(conditions)
                .groupBy(restaurant.id)
                .orderBy(poster.count().desc(), restaurant.placeName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
