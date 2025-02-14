package com.example.travelshooting.part.service;

import com.example.travelshooting.part.dto.PartResDto;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.repository.PartRepository;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class PartService {

    private final ProductService productService;
    private final PartRepository partRepository;
    private final UserService userService;

    public PartResDto createPart(Long productId, LocalTime openAt, LocalTime closeAt, Integer maxQuantity) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductByProductIdAndUserId(productId, user.getId());

        if (product == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 등록할 수 있습니다.");
        }

        // 해당 상품에 이미 등록되어 있는 일정인지 확인
        if (partRepository.existsByProductIdAndOpenAtAndCloseAt(productId, openAt, closeAt)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 일정이 이미 존재합니다.");
        }

        Part part = new Part(openAt, closeAt, maxQuantity, product);
        Part savedPart = partRepository.save(part);

        return new PartResDto(
                savedPart.getId(),
                savedPart.getOpenAt(),
                savedPart.getCloseAt(),
                savedPart.getMaxQuantity()
        );
    }

    @Transactional
    public PartResDto updatePart(Long productId, Long partId, LocalTime openAt, LocalTime closeAt, Integer maxQuantity) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        Part part = partRepository.findPartById(partId);
        // 일정을 수정하려는 사람이 해당 업체의 소유자인지 확인
        Part findPart = partRepository.findPartByProductIdAndUserIdAndId(product.getId(), user.getId(), partId);
        if (findPart == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 수정할 수 있습니다.");
        }

        // 해당 상품에 이미 등록되어 있는 일정인지 확인
        if (partRepository.existsByProductIdAndOpenAtAndCloseAt(productId, openAt, closeAt)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 일정이 이미 존재합니다.");
        }

        findPart.updatePart(openAt, closeAt, maxQuantity);
        partRepository.save(findPart);

        return new PartResDto(
                findPart.getId(),
                findPart.getOpenAt(),
                findPart.getCloseAt(),
                findPart.getMaxQuantity()
        );
    }

    @Transactional
    public void deletePart(Long productId, Long partId) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        // 일정을 삭제하려는 사람이 해당 업체의 소유자인지 확인
        Part findPart = partRepository.findPartByProductIdAndUserIdAndId(product.getId(), user.getId(), partId);
        if (findPart == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 삭제할 수 있습니다.");
        }
        // 해당 일정에 예약이 존재하면 삭제할 수 없음
        Part reservation = partRepository.findReservationById(partId);
        if(reservation != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 예약이 존재하여 삭제할 수 없습니다.");
        }

        partRepository.delete(findPart);
    }

    public Part findPartByIdAndProductId(Long partId, Long productId) {
        return partRepository.findPartByIdAndProductId(partId, productId);
    }
}
