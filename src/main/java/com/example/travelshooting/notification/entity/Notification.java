package com.example.travelshooting.notification.entity;

import com.example.travelshooting.enums.NotificationStatus;
import com.example.travelshooting.enums.NotificationType;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, length = 30)
    private String subject;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @CreatedDate
    private LocalDateTime createdAt;

    public Notification(User user, Reservation reservation, String subject, NotificationStatus status, NotificationType type) {
        this.user = user;
        this.reservation = reservation;
        this.subject = subject;
        this.status = status;
        this.type = type;
    }
}
