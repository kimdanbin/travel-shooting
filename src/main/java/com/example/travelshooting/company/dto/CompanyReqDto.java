package com.example.travelshooting.company.dto;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyReqDto {

    @NotNull(message = "유저 아이디는 필수 입력 항목입니다.")
    private final Long userId;

    @NotBlank(message = "업체명은 필수 입력 항목입니다.")
    private final String name;

    @NotBlank(message = "업체 설명은 필수 입력 항목입니다.")
    private final String description;

    public Company toEntity(User user) {
        return new Company(user, this.name, this.description);
    }
}
