package com.example.travelshooting.reservation.service;

import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ProductService productService;
    private final PartService partService;
    private final ReservationMailService reservationMailService;

    @Transactional
    public ReservationResDto createReservation(Long productId, Long partId, LocalDate reservationDate, Integer headCount) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        Part part = partService.findPartById(partId);
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

        // 사용자 예약 신청 완료 메일
        reservationMailService.sendMail(user, product, part, reservation, user.getRole(), reservation.getStatus());

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

    @Transactional
    public void deleteReservation(Long productId, Long reservationId) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Reservation reservation = reservationRepository.findReservationByUserIdAndProductIdAndId(user.getId(), product.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        reservation.updateReservation(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResDto> findAllByUserIdAndProductId(Long productId, Pageable pageable) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Page<Reservation> reservations = reservationRepository.findAllByUserIdAndProductId(user.getId(), product.getId(), pageable);

        if (reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return reservations.stream().map(reservation -> new ReservationResDto(
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
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResDto findReservationByUserIdAndProductIdAndId(Long productId, Long reservationId) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Reservation reservation = reservationRepository.findReservationByUserIdAndProductIdAndId(user.getId(), product.getId(), reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

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

    public Reservation findReservationByUserIdAndProductIdAndId(Long userId, Long productId, Long reservationId) {
        return reservationRepository.findReservationByUserIdAndProductIdAndId(userId, productId, reservationId);
    }

    public List<Reservation> cancelExpiredReservations() {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(1).withHour(18).withMinute(0).withSecond(0);
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(expirationTime);

        expiredReservations.forEach(reservation -> reservation.updateReservation(ReservationStatus.EXPIRED));

        return reservationRepository.saveAll(expiredReservations);
    }

    public Reservation findReservationByPaymentIdAndUserId(Long paymentId, Long userId) {
        return reservationRepository.findReservationByPaymentIdAndUserId(paymentId, userId);
    }
}