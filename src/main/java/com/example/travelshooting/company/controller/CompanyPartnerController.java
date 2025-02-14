package com.example.travelshooting.company.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import com.example.travelshooting.company.dto.CompanyResDto;
import com.example.travelshooting.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/partners/companies")
@RequiredArgsConstructor
public class CompanyPartnerController {

    private final CompanyService companyService;

    /**
     * 업체 전체 조회 API
     *
     * @return 전체 업체의 정보를 담고 있는 dto. 성공시 상태코드 200 반환
     */
    @GetMapping
    public ResponseEntity<CommonListResDto<CompanyResDto>> findAllCompanies(Pageable pageable) {

        Page<CompanyResDto> result = companyService.findAllCompanies(pageable);
        List<CompanyResDto> content = result.getContent();
        return new ResponseEntity<>(new CommonListResDto<>("업체 전체 조회 완료", content), HttpStatus.OK);
    }

    /**
     * 업체 단건 조회 API
     *
     * @param companyId 조회할 업체의 id
     * @return 조회된 업체의 정보를 담고 있는 dto. 성공시 상태코드 200 반환
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<CommonResDto<CompanyResDto>> findCompany(@PathVariable Long companyId) {

        CompanyResDto result = companyService.findCompany(companyId);

        return new ResponseEntity<>(new CommonResDto<>("업체 단건 조회 완료", result), HttpStatus.OK);
    }
}
