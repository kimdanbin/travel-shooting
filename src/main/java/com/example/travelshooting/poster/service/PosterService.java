package com.example.travelshooting.poster.service;

import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.file.service.PosterFileService;
import com.example.travelshooting.payment.entity.Payment;
import com.example.travelshooting.payment.service.PaymentService;
import com.example.travelshooting.poster.dto.PosterResDto;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.repository.PosterRepository;
import com.example.travelshooting.restaurant.entity.Restaurant;
import com.example.travelshooting.restaurant.service.RestaurantService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PosterService {
    private final PosterRepository posterRepository;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final PaymentService paymentService;
    private final PosterFileService posterFileService;

    // 포스터 생성
    public PosterResDto createPoster(Long restaurantId, Long paymentId, int expenses, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt, List<MultipartFile> files) {

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

        Poster savedPoster = posterRepository.save(poster);
        posterFileService.uploadFile(savedPoster.getId(), files);

        return new PosterResDto(savedPoster);
    }

    // 포스터 단건 조회
    public PosterResDto findPoster(Long posterId) {

        return new PosterResDto(findPosterById(posterId));
    }

    // 포스터 전체 조회
    public Page<PosterResDto> findPosters(Integer minExpenses, Integer maxExpenses, LocalDate travelStartAt, LocalDate travelEndAt, Integer days, Integer month, Pageable pageable) {
        return posterRepository.findPosters(minExpenses, maxExpenses, travelStartAt, travelEndAt, days, month, pageable);
    }

    // 포스터 수정
    @Transactional
    public PosterResDto updatePoster(Long posterId, Long restaurantId, Long paymentId, int expenses, String title, String content, LocalDateTime travelStartAt, LocalDateTime travelEndAt, List<MultipartFile> files) {

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

        Poster savedPoster = posterRepository.save(poster);
        posterFileService.uploadFile(savedPoster.getId(), files);

        return new PosterResDto(savedPoster);
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

    public void deleteReportPoster(Long posterId) {
        posterRepository.deleteById(posterId);
    }
}
