package com.example.travelshooting.company;

import com.example.travelshooting.user.User;
import com.example.travelshooting.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        return new CompanyResDto(
                company.getId(),
                company.getUser().getId(),
                company.getName(),
                company.getDescription(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );

    }
}
