package com.example.travelshooting.user.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResDto implements BaseDtoDataType {

    private final Long id;

    private final String email;

    private final String name;

    private final UserRole role;

    private final String imageUrl;
}
