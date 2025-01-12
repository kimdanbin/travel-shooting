package com.example.travelshooting.reservation.service;

import com.example.travelshooting.part.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    public ReservationResDto createReservation(Long productId, Long partId, LocalDate reservationDate, int number) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authEmail = auth.getName();
        User user = userService.findUserByEmail(authEmail);

        Product product = productService.findProductById(productId);
        Part part = partService.findPartById(partId);

        int totalPrice = product.getPrice() * number;

        Reservation reservation = new Reservation(user, product, part, reservationDate, number, totalPrice);
        reservationRepository.save(reservation);

        return new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getProduct().getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getNumber(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteReservation(Long productId, Long reservationId) {
        productService.findProductById(productId);
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);

        reservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResDto> findAllByUserIdAndProductId(Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authEmail = auth.getName();
        User user = userService.findUserByEmail(authEmail);

        List<Reservation> reservations = reservationRepository.findAllByUserIdAndProductId(user.getId(), productId);

        if (reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return reservations.stream().map(reservation -> new ReservationResDto(
                        reservation.getId(),
                        reservation.getUser().getId(),
                        reservation.getProduct().getId(),
                        reservation.getPart().getId(),
                        reservation.getReservationDate(),
                        reservation.getNumber(),
                        reservation.getTotalPrice(),
                        reservation.getStatus(),
                        reservation.getCreatedAt(),
                        reservation.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResDto findByUserIdAndProductIdAndId(Long productId, Long reservationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authEmail = auth.getName();
        User user = userService.findUserByEmail(authEmail);

        Reservation reservation = reservationRepository.findByUserIdAndProductIdAndId(user.getId(), productId, reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        return new ReservationResDto(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getProduct().getId(),
                reservation.getPart().getId(),
                reservation.getReservationDate(),
                reservation.getNumber(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
