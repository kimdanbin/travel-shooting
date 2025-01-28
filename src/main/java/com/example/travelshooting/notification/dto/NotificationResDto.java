package com.example.travelshooting.notification.dto;

import com.example.travelshooting.common.BaseDtoDataType;
import com.example.travelshooting.enums.NotificationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class NotificationResDto implements BaseDtoDataType {

    private final Long id;
    private final Long reservationId;
    private final String subject;
    private final NotificationType type;
    private final LocalDateTime createdAt;
}
