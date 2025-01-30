package com.example.travelshooting.config;

import com.example.travelshooting.notification.service.NotificationService;
import com.example.travelshooting.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final ReservationService reservationService;
    private final NotificationService notificationService;

    // 예약 만료 처리
    @Scheduled(cron = "0 0 18 * * ?")
    public void cancelExpiredReservations() {
        reservationService.cancelExpiredReservations();
    }

    // 30일 지난 알림 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteNotification() {
        notificationService.deleteNotification();
    }
}