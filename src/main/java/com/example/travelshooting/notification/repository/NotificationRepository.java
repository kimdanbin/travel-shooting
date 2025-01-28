package com.example.travelshooting.notification.repository;

import com.example.travelshooting.enums.NotificationStatus;
import com.example.travelshooting.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdAndStatus(Long userId, NotificationStatus status);
}