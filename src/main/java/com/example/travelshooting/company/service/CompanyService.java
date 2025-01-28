package com.example.travelshooting.company.service;

import com.example.travelshooting.company.dto.CompanyResDto;
import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.company.repository.CompanyRepository;
import com.example.travelshooting.enums.CacheTime;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private static final String CACHE_KEY_PREFIX = "companies:page:";

    @Transactional
    public CompanyResDto createCompany(Long userId, String name, String description) {

        User user = userService.findUserById(userId);
        // 유저가 존재하는지 확인
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다.");
        }
        // 이미 등록된 업체 이름인지 확인
        if (companyRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 업체 이름입니다.");
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
    public List<CompanyResDto> findAllCompanies(int page, int size) {
        final String cacheKey = CACHE_KEY_PREFIX + page;

        if (page == 0) {
            @SuppressWarnings("unchecked")
            List<CompanyResDto> cachedCompanies = (List<CompanyResDto>) redisObjectTemplate.opsForValue().get(cacheKey);
            if (cachedCompanies != null) {
                log.info("캐시에서 첫 번째 페이지 조회: {}", cacheKey);
                return cachedCompanies;
            }
        }

        // 페이지 번호와 크기를 기반으로 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);
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

        // 첫 번째 페이지 캐시에 저장
        if (page == 0) {
            redisObjectTemplate.opsForValue().set(cacheKey, result, CacheTime.COMPANY_CASH_TIMEOUT.getMinutes(), TimeUnit.MINUTES);
            log.info("첫 번째 페이지 캐시 저장: {}", cacheKey);
        }

        return result;
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
        String cacheKey = CACHE_KEY_PREFIX + "0"; // 첫 번째 페이지 캐시 키
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
        String cacheKey = CACHE_KEY_PREFIX + "0"; // 첫 번째 페이지 캐시 키
        redisObjectTemplate.delete(cacheKey);
        log.info("업체 삭제 시 첫 번째 페이지 캐시 삭제: {}", cacheKey);
    }

    public Company findCompanyById(Long companyId) {
        return companyRepository.findCompanyById(companyId);
    }

    public  Company findCompanyByIdAndUserId(Long companyId, Long userId) {
        return companyRepository.findCompanyByIdAndUserId(companyId, userId);
    }

}
