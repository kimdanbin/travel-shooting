package com.example.travelshooting.reservation.service;

import com.example.travelshooting.part.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.Reservation;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userService.getUserById(2L); // 임시 user, 이후 수정 예정
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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 " + reservationId + "에 해당하는 레저/티켓 예약을 찾을 수 없습니다."));

        reservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResDto> findAllByUserIdAndProductId(Long productId) {
        User user = userService.getUserById(2L); // 임시 user, 이후 수정 예정
        List<Reservation> reservations = reservationRepository.findAllByUserIdAndProductId(user.getId(), productId);

        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("아이디 " + productId + "에 해당하는 예약 내역이 없습니다.");
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
        User user = userService.getUserById(2L); // 임시 user, 이후 수정 예정
        Reservation reservation = reservationRepository.findByUserIdAndProductIdAndId(user.getId(), productId, reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("아이디 " + productId + "에 해당하는 예약 내역이 없습니다.");
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
