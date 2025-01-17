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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartService {

    private final ProductService productService;
    private final PartRepository partRepository;
    private final UserService userService;

    public PartResDto createPart(Long productId, LocalTime openAt, LocalTime closeAt, Integer number) {
        Product product = productService.findProductById(productId);
        User user = userService.findAuthenticatedUser();
        // 일정을 등록하려는 사람이 해당 업체의 소유자인지 확인
        if (!product.getCompany().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 등록할 수 있습니다.");
        }
        // 이미 등록되어 있는 일정인지 확인
        if (partRepository.existsByOpenAtAndCloseAt(openAt, closeAt)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 일정이 이미 존재합니다.");
        }
        Part part = new Part(openAt, closeAt, number, product);
        Part savedPart = partRepository.save(part);

        return new PartResDto(
                savedPart.getId(),
                savedPart.getOpenAt(),
                savedPart.getCloseAt(),
                savedPart.getNumber()
        );
    }

    @Transactional
    public PartResDto updatePart(Long partId, LocalTime openAt, LocalTime closeAt, Integer number) {
        Part findPart = partRepository.findPartByIdOrElseThrow(partId);
        User user = userService.findAuthenticatedUser();
        // 일정을 수정하려는 사람이 해당 업체의 소유자인지 확인
        if (!findPart.getProduct().getCompany().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 수정할 수 있습니다.");
        }
        findPart.updatePart(openAt, closeAt, number);
        partRepository.save(findPart);

        return new PartResDto(
                findPart.getId(),
                findPart.getOpenAt(),
                findPart.getCloseAt(),
                findPart.getNumber()
        );
    }

    @Transactional
    public void deleteCompany(Long partId) {
        Part findPart = partRepository.findPartByIdOrElseThrow(partId);
        User user = userService.findAuthenticatedUser();
        // 일정을 삭제하려는 사람이 해당 업체의 소유자인지 확인
        if (!findPart.getProduct().getCompany().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체의 소유자만 일정을 삭제할 수 있습니다.");
        }
        partRepository.delete(findPart);
    }

    public Part findPartById(Long partId) {
        return partRepository.findPartByIdOrElseThrow(partId);
    }

    public Part findPartByProductId(Long productId) {
        Part part = partRepository.findPartByProductId(productId);

        if (part == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + part.getId() + "에 해당하는 레저/티켓 일정을 찾을 수 없습니다.");
        }

        return part;
    }

    public List<Part> findPartsByProductId(Long productId) {
        List<Part> parts = partRepository.findPartsByProductId(productId);

        if (parts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + productId + "에 해당하는 레저/티켓 일정을 찾을 수 없습니다.");
        }

        return parts;
    }

}
