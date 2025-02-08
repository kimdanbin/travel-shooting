package com.example.travelshooting.payment.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.PaymentStatus;
import com.example.travelshooting.enums.RefundType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResDto implements BaseDtoDataType {

    private final Long id;

    private final Long reservationId;

    private final String productName;

    private final Integer headCount;

    private final Integer totalPrice;

    private final String type;

    private final PaymentStatus status;

    private final RefundType refundType;

    private final Integer cancelPrice;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @QueryProjection
    public PaymentResDto(Long id, Long reservationId, String productName, Integer headCount, Integer totalPrice,
                         String type, PaymentStatus status, RefundType refundType, Integer cancelPrice,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.productName = productName;
        this.headCount = headCount;
        this.totalPrice = totalPrice;
        this.type = type;
        this.status = status;
        this.refundType =refundType;
        this.cancelPrice = cancelPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
