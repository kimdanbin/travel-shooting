package com.example.travelshooting.company.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.company.dto.CompanyResDto;
import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.repository.CompanyRepository;
import com.example.travelshooting.config.util.CacheKeyUtil;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisObjectTemplate;

    @Transactional
    public CompanyResDto createCompany(Long userId, String name, String description) {

        User user = userService.findUserById(userId);
        // 유저가 존재하는지 확인
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다.");
        }

        // 관리자는 본인 업체 등록 불가
        if(user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ADMIN 계정은 PARTNER로 등록될 수 없습니다.");
        }

        // 이미 등록된 업체 이름인지 확인
        if (companyRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 업체 이름입니다.");
        }

        // 삭제된 업체명으로 등록하려는 경우 예외처리
        if (companyRepository.findByNameAndIsDeletedTrue(name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 삭제된 업체명은 다시 사용할 수 없습니다.");
        }

        Company company = new Company(user, name, description);
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
    public Page<CompanyResDto> findAllCompanies(Pageable pageable) {

        if(pageable.getPageSize() != Const.PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지 사이즈는 20 만 가능합니다.");
        }

        final String cacheKey = CacheKeyUtil.getCompanyPageKey(pageable.getPageNumber());

        if (pageable.getPageNumber() == 0) {
            @SuppressWarnings("unchecked")
            List<CompanyResDto> cachedCompanies = (List<CompanyResDto>) redisObjectTemplate.opsForValue().get(cacheKey);
            if (cachedCompanies != null) {
                log.info("캐시에서 첫 번째 페이지 조회: {}", cacheKey);
                return new PageImpl<>(cachedCompanies, pageable, cachedCompanies.size());
            }
        }

        Page<Company> companyPage = companyRepository.findAll(pageable);

        List<CompanyResDto> result = companyPage.stream()
                .map(company -> new CompanyResDto(
                        company.getId(),
                        company.getUser().getId(),
                        company.getName(),
                        company.getDescription(),
                        company.getCreatedAt(),
                        company.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        Page<CompanyResDto> resultPage = new PageImpl<>(result, pageable, companyPage.getTotalElements());

        // 첫 번째 페이지 캐시에 저장
        if (pageable.getPageNumber() == 0) {
            redisObjectTemplate.opsForValue().set(cacheKey, result, Const.COMPANY_CASH_TIMEOUT, TimeUnit.MINUTES);
            log.info("첫 번째 페이지 캐시 저장: {}", cacheKey);
        }

        return resultPage;
    }

    @Transactional(readOnly = true)
    public CompanyResDto findCompany(Long companyId) {

        Company findCompany = companyRepository.findCompanyById(companyId);

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
        Company findCompany = companyRepository.findCompanyById(companyId);
        findCompany.updateCompany(description);
        companyRepository.save(findCompany);

        // 첫 번째 페이지 캐시 삭제
        String cacheKey = CacheKeyUtil.getCompanyPageKey(0); // 첫 번째 페이지 캐시 키
        redisObjectTemplate.delete(cacheKey);
        log.info("업데이트 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);

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
        Company findCompany = companyRepository.findCompanyById(companyId);
        // 해당 업체에 상품이 존재하면 삭제할 수 없음
        Company product = companyRepository.findProductById(companyId);
        if(product != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 상품이 존재하여 삭제할 수 없습니다.");
        }
        companyRepository.delete(findCompany);

        // 첫 번째 페이지 캐시 삭제
        String cacheKey = CacheKeyUtil.getCompanyPageKey(0); // 첫 번째 페이지 캐시 키
        redisObjectTemplate.delete(cacheKey);
        log.info("업체 삭제 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);
    }

    public Company findCompanyByIdAndUserId(Long companyId, Long userId) {
        return companyRepository.findCompanyByIdAndUserId(companyId, userId);
    }
}
