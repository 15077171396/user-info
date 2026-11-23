package com.wxl.backend.util;

import org.springframework.util.StringUtils;

/**
 * 数据脱敏。
 */
public final class DesensitizeUtil {

    private DesensitizeUtil() {
    }

    /**
     * 邮箱脱敏：保留前 2 位 + 域名，如 zhangsan@qq.com -> zh***@qq.com
     */
    public static String maskEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return email;
        }
        int at = email.indexOf('@');
        if (at <= 0) {
            return email;
        }
        String local = email.substring(0, at);
        int keep = Math.min(2, local.length());
        return local.substring(0, keep) + "***" + email.substring(at);
    }
}
