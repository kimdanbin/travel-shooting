package com.example.travelshooting.reservation.service;

import com.example.travelshooting.product.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.dto.ReservationResDto;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationPartnerService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<ReservationResDto> findByProductIdAndUserId(Long productId) {
        User partner = userService.getUserById(4L); // 임시 user, 이후 수정 예정
        Product product = productService.findProductById(productId);

        return reservationRepository.findByProductIdAndUserId(product.getId(), partner.getId());
    }
}
