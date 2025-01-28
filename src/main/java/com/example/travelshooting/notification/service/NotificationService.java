package com.example.travelshooting.notification.service;

import com.example.travelshooting.enums.NotificationStatus;
import com.example.travelshooting.notification.dto.NotificationResDto;
import com.example.travelshooting.notification.entity.Notification;
import com.example.travelshooting.notification.repository.NotificationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResDto> findAllByUserIdAndStatus() {
        User user = userService.findAuthenticatedUser();
        List<Notification> notifications = notificationRepository.findAllByUserIdAndStatus(user.getId(), NotificationStatus.SENT);

        return notifications.stream().map(notification -> new NotificationResDto(
                notification.getId(),
                notification.getDomainType(),
                notification.getFkId(),
                notification.getSubject(),
                notification.getNotificationType(),
                notification.getCreatedAt()))
                .collect(Collectors.toList());
    }
}