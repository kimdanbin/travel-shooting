package com.example.travelshooting.reservation.service;

import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ProductService productService;
    private final PartService partService;

    @Transactional
    public ReservationResDto createReservation(Long productId, Long partId, LocalDate reservationDate, Integer headCount) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Part part = partService.findPartById(partId);

        Integer totalHeadCount = reservationRepository.findTotalHeadCountByPartId(part.getId());

        if (part.getHeadCount() < totalHeadCount + headCount) {
            Integer overHeadCount = Math.abs(part.getHeadCount() - totalHeadCount - headCount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신청 가능한 인원을 초과했습니다. 초과된 인원: " + overHeadCount);
        }

        Integer totalPrice = product.getPrice() * headCount;

        Reservation reservation = new Reservation(user, part, reservationDate, headCount, totalPrice);
        reservationRepository.save(reservation);

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

        reservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResDto> findAllByUserIdAndProductId(Long productId) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductById(productId);
        List<Reservation> reservations = reservationRepository.findAllByUserIdAndProductId(user.getId(), product.getId());

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

        expiredReservations.forEach(reservation -> reservation.updateExpiredReservations());

        return reservationRepository.saveAll(expiredReservations);
    }
}
