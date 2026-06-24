package com.whaleal.ark.cloud.third.sms.util;

/**
 * 轻量文本工具（API 层无 Spring 依赖）
 */
public final class TextUtils {

    private TextUtils() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
