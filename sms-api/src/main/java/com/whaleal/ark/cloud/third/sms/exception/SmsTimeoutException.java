package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 网络超时异常
 * 当请求超过设定的超时时间时抛出
 * 
 */
public class SmsTimeoutException extends SmsException {
    
    /**
     * 超时时间（毫秒）
     */
    private final int timeoutMs;
    
    public SmsTimeoutException(String message, int timeoutMs) {
        super("TIMEOUT", message);
        this.timeoutMs = timeoutMs;
    }
    
    public SmsTimeoutException(String message, int timeoutMs, SmsProviderType providerType) {
        super("TIMEOUT", message, providerType);
        this.timeoutMs = timeoutMs;
    }
    
    public SmsTimeoutException(String message, int timeoutMs, SmsProviderType providerType, Throwable cause) {
        super("TIMEOUT", message, providerType, cause);
        this.timeoutMs = timeoutMs;
    }
    
    public int getTimeoutMs() {
        return timeoutMs;
    }
    
    @Override
    public String getFullErrorMessage() {
        return super.getFullErrorMessage() + String.format(" (超时时间: %dms)", timeoutMs);
    }
} 