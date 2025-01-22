package com.example.travelshooting.config;

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

    @Scheduled(cron = "0 0 18 * * ?")
    public void autoCancelReservations() {
        reservationService.cancelExpiredReservations();
    }
}
