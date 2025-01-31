package com.example.travelshooting.common;

public class Const {

    // 음식점
    public static final String GG_NAME = "경기도";

    // 타임아웃
    public static final int API_CONNECT_TIMEOUT = 5000;
    public static final int API_READ_TIMEOUT = 5000;
    public static final int SMTP_TIMEOUT = 5000;
    public static final int RESERVATION_CASH_TIMEOUT = 5;
    public static final int COMPANY_CASH_TIMEOUT = 5;

    // 카카오
    public static final String KAKAO_PAY_TEST_CID = "TC0ONETIME";
    public static final String KAKAO_PAY_HEADER = "SECRET_KEY";
    public static final String KAKAO_MAP_HEADER = "KakaoAK";

    // 예약 메일 제목
    public static final String RESERVATION_APPLY_SUBJECT = "예약 신청 알림이 왔습니다.";
    public static final String RESERVATION_APPROVED_SUBJECT = "예약 승인 알림이 왔습니다.";
    public static final String RESERVATION_REJECTED_SUBJECT = "예약 거절 알림이 왔습니다.";
    public static final String RESERVATION_CANCELED_SUBJECT = "예약 취소 알림이 왔습니다.";
    public static final String RESERVATION_EXPIRED_SUBJECT = "예약 만료 알림이 왔습니다.";

    // 레디스
    public static final String REDISSON_PREFIX = "redis://";
    public static final String REDISSON_RESERVATION_PREFIX = "reservation:";

    // 기타
    public static final int REPORT_COUNT = 5;
    public static final int LOCAL_PAGE_DEFAULT = 1;
    public static final int RESERVATION_EXPIRED_DAY = 1;
    public static final int RESERVATION_EXPIRED_HOUR = 18;
    public static final int NOTIFICATION_EXPIRED_DAY = 30;
}