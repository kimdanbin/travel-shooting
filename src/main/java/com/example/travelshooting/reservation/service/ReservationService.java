package com.example.travelshooting.reservation.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.service.CompanyService;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.service.ReservationMailService;
import com.example.travelshooting.notification.service.SendEmailEvent;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.entity.QPart;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.entity.QProduct;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.dto.QReservationResDto;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.QReservation;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.QUser;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserService userService;
    private final ProductService productService;
    private final PartService partService;
    private final CompanyService companyService;
    private final ReservationMailService reservationMailService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private static final String CACHE_KEY_PREFIX = "reservations:product:";

    @Transactional
    public ReservationResDto createReservation(Long productId, Long partId, LocalDate reservationDate, Integer headCount) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        Part part = partService.findPartById(partId);
        Company company = companyService.findCompanyByProductId(product.getId());
        User partner = userService.findUserByCompanyId(company.getId());
        Optional<Reservation> findReservation = reservationRepository.findReservationByUserIdAndReservationDate(user.getId(), reservationDate);
        Integer totalHeadCount = reservationRepository.findTotalHeadCountByPartIdAndReservationDate(part.getId(), reservationDate);

        if (findReservation.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 날짜에 예약한 내역이 있습니다.");
        }

        if (reservationDate.isBefore(product.getSaleStartAt()) || reservationDate.isAfter(product.getSaleEndAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 날짜는 상품의 판매 기간 중에서만 선택할 수 있습니다.");
        }

        if (part.getMaxQuantity() < totalHeadCount + headCount) {
            Integer overHeadCount = Math.abs(part.getMaxQuantity() - totalHeadCount - headCount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신청 가능한 인원을 초과했습니다. 초과된 인원: " + overHeadCount);
        }

        Integer totalPrice = product.getPrice() * headCount;

        Reservation reservation = new Reservation(user, part, reservationDate, headCount, totalPrice);
        reservationRepository.save(reservation);

        // 메일
        reservationMailService.sendMail(user, product, part, reservation, user.getName());
        reservationMailService.sendMail(partner, product, part, reservation, user.getName());

        return new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                product.getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getHeadCount(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<ReservationResDto> findAllByUserIdAndProductId(Long productId, Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();
        User authenticatedUser = userService.findAuthenticatedUser();

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

    @Transactional(readOnly = true)
    public ReservationResDto findReservationByProductIdAndId(Long productId, Long reservationId) {
        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QProduct product = QProduct.product;
        QPart part = QPart.part;

        BooleanBuilder conditions = new BooleanBuilder();
        User authenticatedUser = userService.findAuthenticatedUser();

        conditions.and(product.id.eq(productId));
        conditions.and(user.id.eq(authenticatedUser.getId()));
        conditions.and(reservation.id.eq(reservationId));

        ReservationResDto result = jpaQueryFactory
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

        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return result;
    }

    @Transactional
    public void deleteReservation(Long productId, Long reservationId) {
        User user = userService.findAuthenticatedUser();
        Reservation reservation = reservationRepository.findReservationByUserIdAndProductIdAndId(user.getId(), productId, reservationId);
        Company company = companyService.findCompanyByProductId(productId);
        User partner = userService.findUserByCompanyId(company.getId());
        Part part = partService.findPartByReservationId(reservation.getId());

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        reservation.updateReservation(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);

        // 메일
        reservationMailService.sendMail(user, part.getProduct(), part, reservation, user.getName());
        reservationMailService.sendMail(partner, part.getProduct(), part, reservation, user.getName());

        // 예약 취소 시 첫 번째 페이지 캐시 삭제
        final String cacheKey = CACHE_KEY_PREFIX + productId + ":page:0";
        redisObjectTemplate.delete(cacheKey);
        log.info("예약 취소 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);
    }

    @Transactional
    public void cancelExpiredReservations() {
        List<Reservation> approvedReservations = reservationRepository.findAllByStatus(ReservationStatus.APPROVED);

        approvedReservations.forEach(reservation -> {
            boolean isPaid = reservation.getPayments().stream()
                    .anyMatch(payment -> payment.getStatus() == PaymentStatus.APPROVED);

            LocalDateTime expirationTime = reservation.getUpdatedAt().plusDays(Const.RESERVATION_EXPIRED_DAY).withHour(Const.RESERVATION_EXPIRED_HOUR).withMinute(0).withSecond(0).withNano(0);

            if (!isPaid && LocalDateTime.now().isAfter(expirationTime)) {
                reservation.updateReservation(ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);

                eventPublisher.publishEvent(new SendEmailEvent(this, reservation));
            }
        });
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendExpiredReservationMail(SendEmailEvent<Reservation> event) {
        Reservation reservation = event.getData();

        Part part = partService.findPartByReservationId(reservation.getId());
        Product product = productService.findProductByPartId(part.getId());
        Company company = companyService.findCompanyByProductId(product.getId());
        User partner = userService.findUserByCompanyId(company.getId());
        User user = userService.findUserByReservationId(reservation.getId());

        reservationMailService.sendMail(user, product, part, reservation, user.getName());
        reservationMailService.sendMail(partner, product, part, reservation, user.getName());
    }

    public Reservation findReservationByUserIdAndProductIdAndId(Long userId, Long productId, Long reservationId) {
        return reservationRepository.findReservationByUserIdAndProductIdAndId(userId, productId, reservationId);
    }

    public Reservation findReservationByPaymentIdAndUserId(Long paymentId, Long userId) {
        return reservationRepository.findReservationByPaymentIdAndUserId(paymentId, userId);
    }
}