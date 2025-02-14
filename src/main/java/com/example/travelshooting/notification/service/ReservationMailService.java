package com.example.travelshooting.notification.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.DomainType;
import com.example.travelshooting.enums.NotificationStatus;
import com.example.travelshooting.enums.NotificationType;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.notification.dto.NotificationDetails;
import com.example.travelshooting.notification.entity.Notification;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationMailService {

    private final MailService mailService;
    private final NotificationService notificationService;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendReservationMail(SendEmailEvent<Reservation> event) {
        Reservation reservation = event.getData();

        sendMail(reservation.getUser(), reservation.getPart().getProduct(), reservation.getPart(), reservation, reservation.getUser().getName());
        sendMail(reservation.getPart().getProduct().getCompany().getUser(), reservation.getPart().getProduct(), reservation.getPart(), reservation, reservation.getUser().getName());
    }

    // 예약 메일 전송
    public void sendMail(User user, Product product, Part part, Reservation reservation, String userName) {
        Map<ReservationStatus, NotificationDetails> detailsMap = reservationDetails();
        NotificationDetails details = detailsMap.get(reservation.getStatus());

        String content = mailContent(user, product, part, reservation, userName);
        mailService.sendMail(user.getEmail(), details.subject(), content);

        // 알림 저장
        notificationService.save(new Notification(user, DomainType.RESERVATION, reservation.getId(), details.subject(), NotificationStatus.SENT, details.type()));
    }

    // 예약 메일 내용에 들어갈 데이터
    public String mailContent(User user, Product product, Part part, Reservation reservation, String userName) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("role", user.getRole().name());
        contents.put("status", reservation.getStatus().name());
        contents.put("userName", userName); // 예약자 이름
        contents.put("reservationDate", reservation.getReservationDate());
        contents.put("productName", product.getName());
        contents.put("partOpenAt", part.getOpenAt());
        contents.put("partCloseAt", part.getCloseAt());
        contents.put("headCount", reservation.getHeadCount());
        contents.put("createdAt", reservation.getCreatedAt());

        return mailService.processTemplate("mail/reservation.html", contents);
    }

    public Map<ReservationStatus, NotificationDetails> reservationDetails() {
        return Map.of(
                ReservationStatus.APPROVED, new NotificationDetails(Const.RESERVATION_APPROVED_SUBJECT, NotificationType.RESERVATION_APPROVED),
                ReservationStatus.REJECTED, new NotificationDetails(Const.RESERVATION_REJECTED_SUBJECT, NotificationType.RESERVATION_REJECTED),
                ReservationStatus.PENDING, new NotificationDetails(Const.RESERVATION_APPLY_SUBJECT, NotificationType.RESERVATION_APPLY),
                ReservationStatus.CANCELED, new NotificationDetails(Const.RESERVATION_CANCELED_SUBJECT, NotificationType.RESERVATION_CANCELED),
                ReservationStatus.EXPIRED, new NotificationDetails(Const.RESERVATION_EXPIRED_SUBJECT, NotificationType.RESERVATION_EXPIRED)
        );
    }
}