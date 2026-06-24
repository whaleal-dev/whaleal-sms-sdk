package com.whaleal.ark.cloud.third.sms.util;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 对外 HTTP 请求 URL 工具：发信等出站调用优先使用 HTTPS。
 */
@Slf4j
public final class HttpUrlUtils {

    private HttpUrlUtils() {
    }

    /**
     * 对外发信 URL 优先 HTTPS（默认开启 SSL）。
     */
    public static String preferHttps(String url) {
        return preferHttps(url, true);
    }

    /**
     * 根据配置决定是否将 HTTP 升级为 HTTPS。
     *
     * @param url       原始 URL
     * @param sslEnabled 是否启用 SSL；null 视为 true
     */
    public static String preferHttps(String url, Boolean sslEnabled) {
        if (url == null || url.isBlank()) {
            return url;
        }
        String trimmed = url.trim();
        boolean useSsl = sslEnabled == null || sslEnabled;

        if (!useSsl) {
            return ensureScheme(trimmed, false);
        }

        if (startsWithIgnoreCase(trimmed, "http://")) {
            String upgraded = "https://" + trimmed.substring(7);
            log.debug("对外发信 URL 已升级为 HTTPS: {} -> {}", trimmed, upgraded);
            return upgraded;
        }

        return ensureScheme(trimmed, true);
    }

    public static String resolveOutboundUrl(String url, SmsProviderConfig config) {
        Boolean sslEnabled = config != null ? config.getSslEnabled() : true;
        return preferHttps(url, sslEnabled);
    }

    private static String ensureScheme(String url, boolean https) {
        if (startsWithIgnoreCase(url, "http://") || startsWithIgnoreCase(url, "https://")) {
            return url;
        }
        return (https ? "https://" : "http://") + url;
    }

    private static boolean startsWithIgnoreCase(String value, String prefix) {
        return value.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
