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

    public PartResDto createPart(Long productId, LocalTime openAt, LocalTime closeAt, Integer headCount) {
        User user = userService.findAuthenticatedUser();
        Product product = productService.findProductByProductIdAndUserId(productId, user.getId());
        Integer totalHeadCount = partRepository.findTotalHeadCountByProductId(product.getId());

        if (product == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 등록할 수 있습니다.");
        }

        // 해당 상품에 이미 등록되어 있는 일정인지 확인
        if (partRepository.existsByProductIdAndOpenAtAndCloseAt(productId, openAt, closeAt)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 일정이 이미 존재합니다.");
        }

        if (product.getQuantity() < totalHeadCount + headCount) {
            Integer overHeadCount = Math.abs(product.getQuantity() - totalHeadCount - headCount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "등록하려는 인원 수가 사용 가능한 상품의 수량을 "+overHeadCount+"만큼 초과했습니다.");
        }

        Part part = new Part(openAt, closeAt, headCount, product);
        Part savedPart = partRepository.save(part);

        return new PartResDto(
                savedPart.getId(),
                savedPart.getOpenAt(),
                savedPart.getCloseAt(),
                savedPart.getHeadCount()
        );
    }

    @Transactional
    public PartResDto updatePart(Long productId, Long partId, LocalTime openAt, LocalTime closeAt, Integer headCount) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        Integer totalHeadCount = partRepository.findTotalHeadCountByProductId(product.getId());
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

        if (product.getQuantity() < totalHeadCount - part.getHeadCount() + headCount) {
            Integer overHeadCount = Math.abs(product.getQuantity() - totalHeadCount + part.getHeadCount() - headCount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정하려는 인원 수가 사용 가능한 상품의 수량을 "+overHeadCount+"만큼 초과했습니다.");
        }

        findPart.updatePart(openAt, closeAt, headCount);
        partRepository.save(findPart);

        return new PartResDto(
                findPart.getId(),
                findPart.getOpenAt(),
                findPart.getCloseAt(),
                findPart.getHeadCount()
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

        partRepository.delete(findPart);
    }

    public Part findPartById(Long partId) {
        return partRepository.findPartById(partId);
    }
}
