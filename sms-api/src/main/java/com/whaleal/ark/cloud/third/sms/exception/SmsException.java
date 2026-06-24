package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS SDK 基础异常类
 * 
 */
public class SmsException extends RuntimeException {
    
    /**
     * 错误代码
     */
    private final String errorCode;
    
    /**
     * 提供商类型
     */
    private final SmsProviderType providerType;
    
    /**
     * 错误详细信息
     */
    private final Object errorDetails;
    
    public SmsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.providerType = null;
        this.errorDetails = null;
    }
    
    public SmsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.providerType = null;
        this.errorDetails = null;
    }
    
    public SmsException(String errorCode, String message, SmsProviderType providerType) {
        super(message);
        this.errorCode = errorCode;
        this.providerType = providerType;
        this.errorDetails = null;
    }
    
    public SmsException(String errorCode, String message, SmsProviderType providerType, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.providerType = providerType;
        this.errorDetails = null;
    }
    
    public SmsException(String errorCode, String message, SmsProviderType providerType, Object errorDetails, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.providerType = providerType;
        this.errorDetails = errorDetails;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public SmsProviderType getProviderType() {
        return providerType;
    }
    
    public Object getErrorDetails() {
        return errorDetails;
    }
    
    /**
     * 获取完整的错误信息
     */
    public String getFullErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorCode).append("]");
        if (providerType != null) {
            sb.append("[").append(providerType.getDisplayName()).append("]");
        }
        sb.append(" ").append(getMessage());
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getFullErrorMessage();
    }
} 