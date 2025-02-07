package com.example.travelshooting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefundPolicy {
    FULL_REFUND(0, 1.0, "100% 환불"),
    PARTIAL_REFUND(1, 0.3, "30% 환불");

    private final int daysBefore; // 며칠 전
    private final double refundRate;
    private final String description;
}
