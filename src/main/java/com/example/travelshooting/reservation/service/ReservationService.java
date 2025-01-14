package com.example.travelshooting.reservation.service;

import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        User user = userService.getAuthenticatedUser();
        Product product = productService.findProductById(productId);
        Part part = partService.findPartById(partId);

        int totalNumber = reservationRepository.findTotalNumberByPartId(part.getId());

        if (part.getNumber() < totalNumber + number) {
            int overNumber = Math.abs(part.getNumber() - totalNumber - number);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신청 가능한 인원을 초과했습니다. 초과된 인원: " + overNumber);
        }

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
        User user = userService.getAuthenticatedUser();
        Reservation reservation = reservationRepository.findByUserIdAndProductIdAndId(user.getId(), productId, reservationId);

        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "예약 내역이 없습니다.");
        }

        reservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResDto> findAllByUserIdAndProductId(Long productId) {
        User user = userService.getAuthenticatedUser();
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
        User user = userService.getAuthenticatedUser();
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

    public Reservation findByProductIdAndId(Long productId, Long reservationId) {
        return reservationRepository.findByProductIdAndId(productId, reservationId);
    }
}
