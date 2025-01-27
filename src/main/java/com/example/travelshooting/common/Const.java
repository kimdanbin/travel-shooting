package com.example.travelshooting.common;

public class Const {

    // 음식점
    public static final String GG_NAME = "경기도";

    // 타임아웃
    public static final int API_CONNECT_TIMEOUT = 5000;
    public static final int API_READ_TIMEOUT = 5000;
    public static final int SMTP_TIMEOUT = 5000;

    // 카카오
    public static final String KAKAO_PAY_TEST_CID = "TC0ONETIME";
    public static final String KAKAO_PAY_HEADER = "SECRET_KEY";
    public static final String KAKAO_MAP_HEADER = "KakaoAK";

    // 예약 메일 제목
    public static final String USER_RESERVATION_APPLY_SUBJECT = "예약 신청이 완료되었습니다.";
    public static final String USER_RESERVATION_APPROVED_SUBJECT = "예약이 승인되었습니다.";
    public static final String USER_RESERVATION_REJECTED_SUBJECT = "예약이 거절되었습니다.";
    public static final String PARTNER_RESERVATION_APPLY_SUBJECT = "새로운 예약 신청이 접수되었습니다.";
    public static final String RESERVATION_CANCELED_SUBJECT = "예약이 취소되었습니다.";

    public static final int REPORT_COUNT = 5;
    public static final int LOCAL_PAGE_DEFAULT = 1;
}