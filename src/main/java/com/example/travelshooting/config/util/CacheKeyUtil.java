package com.example.travelshooting.config.util;

import com.example.travelshooting.common.Const;

public class CacheKeyUtil {

    private CacheKeyUtil() {
    }

    public static String getCompanyPageKey(int page) {
        return Const.COMPANY_CACHE_PREFIX + page;
    }

    public static String getReservationProductPageKey(Long productId, int page) {
        return Const.RESERVATION_CACHE_PREFIX + productId + ":page:" + page;
    }
}
