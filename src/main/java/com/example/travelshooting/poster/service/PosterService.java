package com.example.travelshooting.poster.service;

import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.like.entity.QLikePoster;
import com.example.travelshooting.payment.entity.Payment;
import com.example.travelshooting.payment.service.PaymentService;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.dto.QSearchPosterResDto;
import com.example.travelshooting.poster.dto.SearchPosterResDto;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.entity.QPoster;
import com.example.travelshooting.poster.repository.PosterRepository;
import com.example.travelshooting.restaurant.entity.Restaurant;
import com.example.travelshooting.restaurant.service.RestaurantService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PosterService {
    private final PosterRepository posterRepository;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final PaymentService paymentService;
    private final JPAQueryFactory jpaQueryFactory;

    // 포스터 생성
    public PosterResDto createPoster(Long restaurantId, Long paymentId, int expenses, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt) {

        User user = userService.findAuthenticatedUser();
        Restaurant restaurant = restaurantId != null ? restaurantService.findRestaurantById(restaurantId) : null;
        Payment payment = paymentService.findPaymentById(paymentId);

        // 로그인 유저가 결제한 내역이 아닐 경우
        if (!user.getId().equals(payment.getReservation().getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인이 결제한 항목이 아닙니다.");
        }

        Poster poster = Poster.builder()
                .user(user)
                .restaurant(restaurant)
                .payment(payment)
                .expenses(expenses)
                .title(title)
                .content(content)
                .travelStartAt(travelStartAt)
                .travelEndAt(travelEndAt)
                .build();

        return new PosterResDto(posterRepository.save(poster));
    }

    // 포스터 단건 조회
    public PosterResDto findPoster(Long posterId) {

        return new PosterResDto(findPosterById(posterId));
    }
    // 포스터 전체 조회
    public Page<SearchPosterResDto> findAll(Integer minExpenses, Integer maxExpenses, LocalDate travelStartAt, LocalDate travelEndAt, Pageable pageable) {
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

        QueryResults<SearchPosterResDto> queryResults = jpaQueryFactory
            .select(new QSearchPosterResDto(
                poster.id,
                poster.title,
                poster.expenses,
                poster.travelStartAt,
                poster.travelEndAt,
                poster.likePosters.size()))
            .from(poster)
            .leftJoin(likePoster).on(poster.id.eq(likePoster.poster.id))
            .where(conditions)
            .groupBy(poster.id, poster.title, poster.expenses, poster.travelStartAt, poster.travelEndAt)
            .orderBy(likePoster.count().desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    // 포스터 수정
    @Transactional
    public PosterResDto updatePoster(Long posterId, Long restaurantId, Long paymentId, int expenses, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt) {

        User user = userService.findAuthenticatedUser();
        Poster poster = findPosterById(posterId);

        // 본인이 작성한 포스터가 아닐 경우
        if (!user.getId().equals(poster.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 포스터만 수정 가능합니다.");
        }

        Restaurant restaurant = restaurantId != null ? restaurantService.findRestaurantById(restaurantId) : null;
        Payment payment = paymentService.findPaymentById(paymentId);

        // 로그인 유저가 결제한 내역이 아닐 경우
        if (!user.getId().equals(payment.getReservation().getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인이 결제한 항목이 아닙니다.");
        }

        poster.updateRestaurant(restaurant);
        poster.updatePayment(payment);
        poster.updateExpenses(expenses);
        poster.updateTitle(title);
        poster.updateContent(content);
        poster.updateTravelStartAt(travelStartAt);
        poster.updateTravelEndAt(travelEndAt);

        return new PosterResDto(posterRepository.save(poster));
    }

    // 포스터 삭제
    public void deletePoster(Long posterId) {

        User user = userService.findAuthenticatedUser();
        Poster poster = findPosterById(posterId);

        // 관리자가 아니거나 본인이 작성한 포스터가 아닐 경우
        if (!user.getRole().equals(UserRole.ADMIN) && !user.getId().equals(poster.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 포스터만 삭제 가능합니다.");
        }

        posterRepository.deleteById(posterId);
    }

    // 포스터 아이디로 포스터 찾기
    public Poster findPosterById(Long posterId) {
        return posterRepository.findById(posterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + posterId + "에 해당하는 포스터를 찾을 수 없습니다."));
    }

    // isDeleted 가 true 인 포스터도 포함해서 찾기
    public Optional<Poster> findByIdIncludeDeleted(Long posterId) {
        return posterRepository.findByIdIncludeDeleted(posterId);
    }
}
