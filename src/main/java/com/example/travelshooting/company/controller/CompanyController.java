package com.example.travelshooting.company.controller;

import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.company.dto.CompanyReqDto;
import com.example.travelshooting.company.dto.CompanyResDto;
import com.example.travelshooting.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 업체 생성 API
     *
     * @param companyReqDto 생성할 업체의 정보를 담고있는 dto
     * @return 생성된 가게의 정보를 담고 있는 dto. 성공시 상태코드 201 반환
     */
    @PostMapping
    @RequestMapping("/admins/companies")
    public ResponseEntity<CommonResDto<CompanyResDto>> createCompany(
            @Valid @RequestBody CompanyReqDto companyReqDto
    ) {
        CompanyResDto result = companyService.createCompany(companyReqDto);

        return new ResponseEntity<>(new CommonResDto<>("업체 생성 완료", result), HttpStatus.CREATED);
    }

}
