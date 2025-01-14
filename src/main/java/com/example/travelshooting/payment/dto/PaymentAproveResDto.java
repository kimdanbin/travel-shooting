package com.example.travelshooting.payment.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentAproveResDto implements BaseDtoDataType {
    // 요청 고유 번호
    private final String aid;

    // 결제 고유 번호
    private final String tid;

    @JsonProperty("partner_order_id")
    private final String reservationId;

    @JsonProperty("partner_user_id")
    private final String userId;

    @JsonProperty("item_name")
    private final String productName;

    @JsonProperty("quantity")
    private final int number;

    @JsonProperty("total")
    private final Integer totalPrice;
}
