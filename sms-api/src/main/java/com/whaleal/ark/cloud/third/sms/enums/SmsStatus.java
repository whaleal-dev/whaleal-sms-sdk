package com.whaleal.ark.cloud.third.sms.enums;

/**
 * SMS发送状态枚举
 * 
 */
public enum SmsStatus {
    
    /**
     * 发送成功
     */
    SUCCESS("SUCCESS", "发送成功"),
    
    /**
     * 发送失败
     */
    FAILED("FAILED", "发送失败"),
    
    /**
     * 等待发送
     */
    PENDING("PENDING", "等待发送"),
    
    /**
     * 发送中
     */
    SENDING("SENDING", "发送中"),
    
    /**
     * 已送达
     */
    DELIVERED("DELIVERED", "已送达"),
    
    /**
     * 送达失败
     */
    DELIVERY_FAILED("DELIVERY_FAILED", "送达失败"),
    
    /**
     * 已过期
     */
    EXPIRED("EXPIRED", "已过期"),
    
    /**
     * 被拒绝
     */
    REJECTED("REJECTED", "被拒绝"),
    
    /**
     * 未知状态
     */
    UNKNOWN("UNKNOWN", "未知状态"),
    
    /**
     * 限流
     */
    THROTTLED("THROTTLED", "限流"),
    
    /**
     * 无效消息
     */
    INVALID_MESSAGE("INVALID_MESSAGE", "无效消息"),
    
    /**
     * 无效凭据
     */
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "无效凭据"),
    
    /**
     * 内部错误
     */
    INTERNAL_ERROR("INTERNAL_ERROR", "内部错误"),
    
    /**
     * 号码被禁
     */
    NUMBER_BARRED("NUMBER_BARRED", "号码被禁"),
    
    /**
     * 合作伙伴账户被禁
     */
    PARTNER_ACCOUNT_BARRED("PARTNER_ACCOUNT_BARRED", "合作伙伴账户被禁"),
    
    /**
     * 合作伙伴配额超限
     */
    PARTNER_QUOTA_EXCEEDED("PARTNER_QUOTA_EXCEEDED", "合作伙伴配额超限");
    
    private final String code;
    private final String description;
    
    SmsStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取状态
     * @param code 状态代码
     * @return 状态枚举
     */
    public static SmsStatus fromCode(String code) {
        for (SmsStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return UNKNOWN;
    }
    
    /**
     * 是否为最终状态
     * @return true表示是最终状态
     */
    public boolean isFinalStatus() {
        return this == SUCCESS || this == FAILED || this == DELIVERED || 
               this == DELIVERY_FAILED || this == EXPIRED || this == REJECTED;
    }
    
    /**
     * 是否为成功状态
     * @return true表示成功
     */
    public boolean isSuccess() {
        return this == SUCCESS || this == DELIVERED;
    }
    
    /**
     * 是否为失败状态
     * @return true表示失败
     */
    public boolean isFailure() {
        return this == FAILED || this == DELIVERY_FAILED || 
               this == EXPIRED || this == REJECTED;
    }
} 