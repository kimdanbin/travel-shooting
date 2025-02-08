package com.example.travelshooting.notification.dto;

import com.example.travelshooting.enums.NotificationType;

public record NotificationDetails(String subject, NotificationType type) {
}