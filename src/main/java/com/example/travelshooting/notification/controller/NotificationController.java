package com.example.travelshooting.notification.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.notification.dto.NotificationResDto;
import com.example.travelshooting.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CommonListResDto<NotificationResDto>> findAllByUserIdAndStatus() {
        List<NotificationResDto> all = notificationService.findAllByUserIdAndStatus();

        return new ResponseEntity<>(new CommonListResDto<>("알림 내역 전체 조회 완료", all), HttpStatus.OK);
    }
}
