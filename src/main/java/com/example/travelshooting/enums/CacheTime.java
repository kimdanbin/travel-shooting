package com.example.travelshooting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheTime {
    RESERVATION_CASH_TIMEOUT(5),
    COMPANY_CASH_TIMEOUT(5);

    private final int minutes;
}
