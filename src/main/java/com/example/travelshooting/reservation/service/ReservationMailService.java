package com.example.travelshooting.reservation.service;

import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.ReservationStatus;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.notification.service.MailService;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.reservation.entity.Reservation;
import com.example.travelshooting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationMailService {

    private final MailService mailService;

    // 예약 메일 전송
    public void sendMail(User user, Product product, Part part, Reservation reservation, UserRole role, ReservationStatus status) {
        String subject = mailSubject(role, status);
        String content = mailContent(user, product, part, reservation);

        mailService.sendMail(user.getEmail(), subject, content);
    }

    // 권한에 따른 메일 제목 적용
    public String mailSubject(UserRole role, ReservationStatus status) {
        if (role.equals(UserRole.USER)) {
            return userSubject(status);
        }

        if (role.equals(UserRole.PARTNER)) {
            return partnerSubject(status);
        }

        if (status.equals(ReservationStatus.EXPIRED)) {
            return Const.RESERVATION_CANCELED_SUBJECT;
        }

        return "알 수 없음";
    }

    public String userSubject(ReservationStatus status) {
        switch (status) {
            case PENDING:
                return Const.USER_RESERVATION_APPLY_SUBJECT;
            case APPROVED:
                return Const.USER_RESERVATION_APPROVED_SUBJECT;
            case REJECTED:
                return Const.USER_RESERVATION_REJECTED_SUBJECT;
            default:
                return "예약 상태 알림";
        }
    }

    public String partnerSubject(ReservationStatus status) {
        switch (status) {
            case PENDING:
                return Const.PARTNER_RESERVATION_APPLY_SUBJECT;
            case APPROVED:
                return Const.PARTNER_RESERVATION_APPROVED_SUBJECT;
            case REJECTED:
                return Const.PARTNER_RESERVATION_REJECTED_SUBJECT;
            default:
                return "업체 예약 상태 알림";
        }
    }

    // 예약 메일 내용에 들어갈 데이터
    public String mailContent(User user, Product product, Part part, Reservation reservation) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("role", user.getRole().name());
        contents.put("status", reservation.getStatus().name());
        contents.put("userName", user.getName());
        contents.put("reservationDate", reservation.getReservationDate());
        contents.put("productName", product.getName());
        contents.put("partOpenAt", part.getOpenAt());
        contents.put("partCloseAt", part.getCloseAt());
        contents.put("headCount", reservation.getHeadCount());
        contents.put("createdAt", reservation.getCreatedAt());

        return mailService.processTemplate("/mail/reservation", contents);
    }
}
