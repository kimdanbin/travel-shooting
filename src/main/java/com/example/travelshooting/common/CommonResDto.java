package com.example.travelshooting.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class CommonResDto<T> {

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public CommonResDto(String message, T data) {
        this.message = message;

        if (data != null && !(data instanceof BaseDtoDataType)) {
            throw new IllegalArgumentException("data는 BaseDtoDataType을 상속해야 합니다.");
        }

        this.data = data;
    }

    public CommonResDto(String message) {
        this(message, null);
    }
}
