package com.example.travelshooting.payment.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCancelResDto implements BaseDtoDataType {

    private final String status;
    private final String reservationId;
    private final String userId;
    private final String type;
    private final String productName;
    private final Integer headCount;
    private final Integer cancelPrice;
    private final String canceledAt;
}
