package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 网络异常
 * 当网络连接失败、DNS解析失败等网络问题时抛出
 * 
 */
public class SmsNetworkException extends SmsException {
    
    /**
     * HTTP状态码（如果适用）
     */
    private final Integer httpStatusCode;
    
    /**
     * 请求URL（如果适用）
     */
    private final String requestUrl;
    
    public SmsNetworkException(String message) {
        super("NETWORK_ERROR", message);
        this.httpStatusCode = null;
        this.requestUrl = null;
    }
    
    public SmsNetworkException(String message, SmsProviderType providerType) {
        super("NETWORK_ERROR", message, providerType);
        this.httpStatusCode = null;
        this.requestUrl = null;
    }
    
    public SmsNetworkException(String message, SmsProviderType providerType, Throwable cause) {
        super("NETWORK_ERROR", message, providerType, cause);
        this.httpStatusCode = null;
        this.requestUrl = null;
    }
    
    public SmsNetworkException(String message, SmsProviderType providerType, Integer httpStatusCode, String requestUrl, Throwable cause) {
        super("NETWORK_ERROR", message, providerType, cause);
        this.httpStatusCode = httpStatusCode;
        this.requestUrl = requestUrl;
    }
    
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
    
    public String getRequestUrl() {
        return requestUrl;
    }
    
    /**
     * 连接失败
     */
    public static SmsNetworkException connectionFailed(String host, SmsProviderType providerType, Throwable cause) {
        return new SmsNetworkException(
                String.format("无法连接到服务器: %s", host), 
                providerType, 
                cause
        );
    }
    
    /**
     * DNS解析失败
     */
    public static SmsNetworkException dnsResolutionFailed(String host, SmsProviderType providerType, Throwable cause) {
        return new SmsNetworkException(
                String.format("DNS解析失败: %s", host), 
                providerType, 
                cause
        );
    }
    
    /**
     * HTTP错误响应
     */
    public static SmsNetworkException httpError(int statusCode, String reasonPhrase, String requestUrl, SmsProviderType providerType) {
        return new SmsNetworkException(
                String.format("HTTP请求失败: %d %s", statusCode, reasonPhrase), 
                providerType, 
                statusCode, 
                requestUrl, 
                null
        );
    }
    
    /**
     * SSL/TLS握手失败
     */
    public static SmsNetworkException sslHandshakeFailed(String host, SmsProviderType providerType, Throwable cause) {
        return new SmsNetworkException(
                String.format("SSL/TLS握手失败: %s", host), 
                providerType, 
                cause
        );
    }
    
    /**
     * 网络不可达
     */
    public static SmsNetworkException networkUnreachable(String host, SmsProviderType providerType, Throwable cause) {
        return new SmsNetworkException(
                String.format("网络不可达: %s", host), 
                providerType, 
                cause
        );
    }
    
    /**
     * 服务不可用
     */
    public static SmsNetworkException serviceUnavailable(SmsProviderType providerType) {
        return new SmsNetworkException(
                "服务暂时不可用，请稍后重试", 
                providerType
        );
    }
    
    @Override
    public String getFullErrorMessage() {
        String baseMessage = super.getFullErrorMessage();
        if (httpStatusCode != null) {
            baseMessage += String.format(" (HTTP状态码: %d)", httpStatusCode);
        }
        if (requestUrl != null) {
            baseMessage += String.format(" (请求URL: %s)", requestUrl);
        }
        return baseMessage;
    }
} 