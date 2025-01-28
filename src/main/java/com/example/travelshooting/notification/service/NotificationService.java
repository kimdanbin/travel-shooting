package com.example.travelshooting.notification.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.NotificationType;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.dto.NotificationDetails;
import com.example.travelshooting.notification.entity.Notification;
import com.example.travelshooting.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    public Map<ReservationStatus, NotificationDetails> reservationDetails() {
        return Map.of(
                ReservationStatus.APPROVED, new NotificationDetails(Const.RESERVATION_APPROVED_SUBJECT, NotificationType.RESERVATION_APPROVED),
                ReservationStatus.REJECTED, new NotificationDetails(Const.RESERVATION_REJECTED_SUBJECT, NotificationType.RESERVATION_REJECTED)
        );
    }
}