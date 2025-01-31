package com.example.travelshooting.reservation.service;

import com.example.travelshooting.company.entity.QCompany;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.service.ReservationMailService;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.entity.QPart;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.QProduct;
import com.example.travelshooting.reservation.dto.QReservationResDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.QReservation;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserService userService;
    private final PartService partService;
    private final ReservationMailService reservationMailService;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private static final String CACHE_KEY_PREFIX = "reservations:product:";

    @Transactional(readOnly = true)
    public Page<ReservationResDto> findAllByProductIdAndUserId(Long productId, Pageable pageable) {

        final String cacheKey = CACHE_KEY_PREFIX + productId + ":page:" + pageable.getPageNumber();

        // 첫 번째 페이지일 경우 캐시에서 조회
//        if (pageable.getPageNumber() == 0) {
//            @SuppressWarnings("unchecked")
//            List<ReservationResDto> cachedReservations = (List<ReservationResDto>) redisObjectTemplate.opsForValue().get(cacheKey);
//            if (cachedReservations != null) {
//                log.info("캐시에서 예약 첫 번째 페이지 조회: {}", cacheKey);
//                return cachedReservations;
//            }
//        }

        QReservation reservation = QReservation.reservation;
        QCompany company = QCompany.company;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();
        User authenticatedUser = userService.findAuthenticatedUser();

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

        // 첫 번째 페이지일 경우 캐시에 저장
//        if (pageable.getPageNumber() == 0) {
//            redisObjectTemplate.opsForValue().set(cacheKey, queryResults, Const.RESERVATION_CASH_TIMEOUT, TimeUnit.MINUTES);
//            log.info("첫 번째 페이지 캐시 저장: {}", cacheKey);
//        }

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Transactional(readOnly = true)
    public ReservationResDto findReservationByProductIdAndUserIdAndId(Long productId, Long reservationId) {
        QReservation reservation = QReservation.reservation;
        QCompany company = QCompany.company;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();
        User authenticatedUser = userService.findAuthenticatedUser();

        conditions.and(product.id.eq(productId));
        conditions.and(company.user.id.eq(authenticatedUser.getId()));
        conditions.and(reservation.id.eq(reservationId));

        ReservationResDto result = jpaQueryFactory
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

        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return result;
    }

    @Transactional
    public ReservationResDto updateReservationStatus(Long productId, Long reservationId, String status) {
        findReservationByProductIdAndUserIdAndId(productId, reservationId);
        Reservation reservation = reservationRepository.findReservationById(reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        Part part = partService.findPartByReservationId(reservation.getId());
        User user = userService.findUserByReservationId(reservation.getId());

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 수락 또는 거절 상태입니다.");
        }

        reservation.updateStatus(ReservationStatus.valueOf(status));
        Reservation updatedReservation = reservationRepository.save(reservation);

        // 메일
        reservationMailService.sendMail(user, part.getProduct(), part, reservation, user.getName());

        // 상태 업데이트 시 첫 번째 페이지 캐시 삭제
        final String cacheKey = CACHE_KEY_PREFIX + productId + ":page:0";
        redisObjectTemplate.delete(cacheKey);
        log.info("예약 업데이트 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);

        return new ReservationResDto(
                updatedReservation.getId(),
                updatedReservation.getUser().getId(),
                part.getProduct().getId(),
                updatedReservation.getPart().getId(),
                updatedReservation.getReservationDate(),
                updatedReservation.getHeadCount(),
                updatedReservation.getTotalPrice(),
                updatedReservation.getStatus(),
                updatedReservation.getCreatedAt(),
                updatedReservation.getUpdatedAt()
        );
    }
}
