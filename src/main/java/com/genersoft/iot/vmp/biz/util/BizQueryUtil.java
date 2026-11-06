package com.genersoft.iot.vmp.biz.util;

import org.springframework.util.ObjectUtils;

/**
 * 业务档案查询参数兜底
 */
public final class BizQueryUtil {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_COUNT = 10;
    private static final int MAX_COUNT = 500;

    private BizQueryUtil() {
    }

    public static int page(Integer page) {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    public static int count(Integer count) {
        if (count == null || count < 1) {
            return DEFAULT_COUNT;
        }
        return Math.min(count, MAX_COUNT);
    }

    public static String keyword(String keyword) {
        return ObjectUtils.isEmpty(keyword) ? null : keyword.trim();
    }
}
