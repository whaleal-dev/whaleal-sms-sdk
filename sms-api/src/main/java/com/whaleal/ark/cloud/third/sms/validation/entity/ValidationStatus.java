package com.whaleal.ark.cloud.third.sms.validation.entity;

import lombok.Getter;

/**
 * 号码验证状态枚举
 */
@Getter
public enum ValidationStatus {
    
    /**
     * 验证成功
     */
    SUCCESS("success", "验证成功"),
    
    /**
     * 验证失败
     */
    FAILED("failed", "验证失败"),
    
    /**
     * 验证错误
     */
    ERROR("error", "验证错误"),
    
    /**
     * 验证中
     */
    PENDING("pending", "验证中"),
    
    /**
     * 未验证
     */
    UNVERIFIED("unverified", "未验证");
    
    /**
     * 状态代码
     */
    private final String code;
    
    /**
     * 状态描述
     */
    private final String description;
    
    ValidationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
} 