package com.example.travelshooting.common;

import lombok.Getter;

import java.util.List;

@Getter
public class CommonListResDto<T extends BaseDtoDataType> {

    private final String message;
    private final List<T> data;

    public CommonListResDto(String message, List<T> data) {
        this.message = message;
        this.data = data;
    }
}