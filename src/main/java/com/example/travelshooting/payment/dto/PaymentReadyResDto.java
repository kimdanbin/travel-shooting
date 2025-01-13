package com.example.travelshooting.payment.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentReadyResDto implements BaseDtoDataType {
    // 카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한 사용자 정보 입력화면 Redirect URL
    @JsonProperty("next_redirect_pc_url")
    private final String nextRedirectPcUrl;
}
