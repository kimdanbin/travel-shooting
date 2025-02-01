package com.example.travelshooting.payment.dto;

import com.example.travelshooting.enums.RefundType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentDto {

    private final Long paymentId;
    private final Integer totalPrice;
    private final Integer cancelPrice;
    private final RefundType refundType;
}
