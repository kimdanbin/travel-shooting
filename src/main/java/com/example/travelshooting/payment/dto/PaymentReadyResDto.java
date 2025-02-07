package com.example.travelshooting.payment.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentReadyResDto implements BaseDtoDataType {

    @JsonProperty("next_redirect_pc_url") // 결제 요청 URL (QR 결제 및 카톡 결제)
    private final String nextRedirectPcUrl;
}
