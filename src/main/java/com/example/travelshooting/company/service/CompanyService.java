package com.example.travelshooting.company.service;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.dto.CompanyReqDto;
import com.example.travelshooting.company.dto.CompanyResDto;
import com.example.travelshooting.company.repository.CompanyRepository;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;

    @Transactional
    public CompanyResDto createCompany(CompanyReqDto companyReqDto) {

        User user = userService.getUserById(companyReqDto.getUserId());
        Company company = companyReqDto.toEntity(user);
        companyRepository.save(company);
        user.updateRole();

        return new CompanyResDto(
                company.getId(),
                company.getUser().getId(),
                company.getName(),
                company.getDescription(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );

    }

    @Transactional(readOnly = true)
    public List<CompanyResDto> findAllCompanies(int page, int size) {
        // 페이지 번호와 크기를 기반으로 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companyPage = companyRepository.findAll(pageable);

        return companyPage.stream()
                .map(company -> new CompanyResDto(
                        company.getId(),
                        company.getUser().getId(),
                        company.getName(),
                        company.getDescription(),
                        company.getCreatedAt(),
                        company.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyResDto findCompany(Long companyId) {

        Company findCompany = companyRepository.findByIdOrElseThrow(companyId);

        return new CompanyResDto(
                findCompany.getId(),
                findCompany.getUser().getId(),
                findCompany.getName(),
                findCompany.getDescription(),
                findCompany.getCreatedAt(),
                findCompany.getUpdatedAt()
        );
    }

    @Transactional
    public CompanyResDto updateCompany(Long companyId, String description) {
        Company findCompany = companyRepository.findByIdOrElseThrow(companyId);
        findCompany.updateCompany(description);
        companyRepository.save(findCompany);

        return new CompanyResDto(
                findCompany.getId(),
                findCompany.getUser().getId(),
                findCompany.getName(),
                findCompany.getDescription(),
                findCompany.getCreatedAt(),
                findCompany.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteCompany(Long companyId) {
        Company findCompany = companyRepository.findByIdOrElseThrow(companyId);
        companyRepository.delete(findCompany);
    }

    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " +companyId + "에 해당하는 업체를 찾을 수 없습니다."));
    }
}
