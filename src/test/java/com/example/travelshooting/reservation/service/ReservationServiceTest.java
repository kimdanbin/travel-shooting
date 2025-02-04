package com.example.travelshooting.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ReservationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceTest.class);
    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("예약 동시성 제어")
    public void testCreateReservation() throws InterruptedException {
        int threads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threads); // 쓰레드 생성
        CountDownLatch latch = new CountDownLatch(threads); // 주어진 수 만큼 이벤트를 기다림

        Long productId = 1L;
        Long partId = 1L;
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        Integer headCount = 1;

        for (int i = 0; i < threads; i++) {

            int index = i;
            executorService.submit(() -> {
                try {
                    log.info("{}번째 쓰레드 접근 시작", index);
                    assertDoesNotThrow(() -> reservationService.createReservation(productId, partId, reservationDate, headCount));
                } catch (Exception e) {
                    log.error("{}번째 쓰레드에서 예외 발생: {}", index, e.getMessage(), e);
                    fail(index + "번째 스레드에서 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                    log.info("{}번째 쓰레드 접근 종료", index);
                }
            });
        }

        latch.await(); // 모든 쓰레드의 작업이 완료될 때까지 대기
        executorService.shutdown();
    }
}