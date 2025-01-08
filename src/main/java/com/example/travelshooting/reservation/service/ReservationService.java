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
                user.getId(),
                product.getId(),
                part.getId(),
                reservation.getReservationDate(),
                reservation.getNumber(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
