package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 内容违规异常
 * 当短信内容包含敏感词、违规内容或不符合规范时抛出
 * 
 */
public class SmsContentException extends SmsException {
    
    /**
     * 违规内容
     */
    private final String violationContent;
    
    /**
     * 违规类型
     */
    private final String violationType;
    
    /**
     * 敏感词列表
     */
    private final String[] sensitiveWords;
    
    public SmsContentException(String message) {
        super("CONTENT_VIOLATION", message);
        this.violationContent = null;
        this.violationType = null;
        this.sensitiveWords = null;
    }
    
    public SmsContentException(String message, SmsProviderType providerType) {
        super("CONTENT_VIOLATION", message, providerType);
        this.violationContent = null;
        this.violationType = null;
        this.sensitiveWords = null;
    }
    
    public SmsContentException(String message, String violationContent, String violationType, String[] sensitiveWords, SmsProviderType providerType) {
        super("CONTENT_VIOLATION", message, providerType);
        this.violationContent = violationContent;
        this.violationType = violationType;
        this.sensitiveWords = sensitiveWords;
    }
    
    public String getViolationContent() {
        return violationContent;
    }
    
    public String getViolationType() {
        return violationType;
    }
    
    public String[] getSensitiveWords() {
        return sensitiveWords;
    }
    
    /**
     * 包含敏感词
     */
    public static SmsContentException sensitiveWords(String content, String[] sensitiveWords, SmsProviderType providerType) {
        return new SmsContentException(
                String.format("短信内容包含敏感词: %s", String.join(", ", sensitiveWords)), 
                content, 
                "SENSITIVE_WORDS", 
                sensitiveWords, 
                providerType
        );
    }
    
    /**
     * 涉政内容
     */
    public static SmsContentException politicalContent(String content, SmsProviderType providerType) {
        return new SmsContentException(
                "短信内容涉及政治敏感信息", 
                content, 
                "POLITICAL", 
                null, 
                providerType
        );
    }
    
    /**
     * 涉黄内容
     */
    public static SmsContentException adultContent(String content, SmsProviderType providerType) {
        return new SmsContentException(
                "短信内容涉及色情信息", 
                content, 
                "ADULT", 
                null, 
                providerType
        );
    }
    
    /**
     * 暴力内容
     */
    public static SmsContentException violentContent(String content, SmsProviderType providerType) {
        return new SmsContentException(
                "短信内容涉及暴力信息", 
                content, 
                "VIOLENT", 
                null, 
                providerType
        );
    }
    
    /**
     * 广告内容
     */
    public static SmsContentException advertisingContent(String content, SmsProviderType providerType) {
        return new SmsContentException(
                "短信内容包含广告推广信息", 
                content, 
                "ADVERTISING", 
                null, 
                providerType
        );
    }
    
    /**
     * 诈骗内容
     */
    public static SmsContentException fraudContent(String content, SmsProviderType providerType) {
        return new SmsContentException(
                "短信内容涉嫌诈骗信息", 
                content, 
                "FRAUD", 
                null, 
                providerType
        );
    }
    
    /**
     * 内容长度超限
     */
    public static SmsContentException contentTooLong(String content, int maxLength, SmsProviderType providerType) {
        return new SmsContentException(
                String.format("短信内容长度超限: %d 字符 (最大: %d)", content.length(), maxLength), 
                content, 
                "TOO_LONG", 
                null, 
                providerType
        );
    }
    
    /**
     * 内容为空
     */
    public static SmsContentException emptyContent(SmsProviderType providerType) {
        return new SmsContentException(
                "短信内容不能为空", 
                "", 
                "EMPTY", 
                null, 
                providerType
        );
    }
    
    /**
     * 签名未报备
     */
    public static SmsContentException unregisteredSignature(String signature, SmsProviderType providerType) {
        return new SmsContentException(
                String.format("短信签名未报备: %s", signature), 
                signature, 
                "UNREGISTERED_SIGNATURE", 
                null, 
                providerType
        );
    }
    
    @Override
    public String getFullErrorMessage() {
        String baseMessage = super.getFullErrorMessage();
        if (violationType != null) {
            baseMessage += String.format(" (违规类型: %s)", violationType);
        }
        if (sensitiveWords != null && sensitiveWords.length > 0) {
            baseMessage += String.format(" (敏感词: %s)", String.join(", ", sensitiveWords));
        }
        return baseMessage;
    }
} 