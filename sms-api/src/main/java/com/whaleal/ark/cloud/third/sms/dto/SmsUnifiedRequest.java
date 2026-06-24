package com.whaleal.ark.cloud.third.sms.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.whaleal.ark.cloud.third.sms.util.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * 统一短信发送请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsUnifiedRequest {
    
    /**
     * 接收方号码列表
     */
    private List<String> phoneNumbers;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 模板ID
     */
    private String templateId;
    
    /**
     * 模板参数
     */
    private Map<String, Object> templateParams;
    
    /**
     * 发送方号码
     */
    private String fromNumber;
    
    /**
     * 签名
     */
    private String signature;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 调度时间
     */
    private Long scheduledTime;
    
    /**
     * 回调URL
     */
    private String callbackUrl;
    
    /**
     * 自定义参数
     */
    private Map<String, Object> customParams;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 应用ID
     */
    private String applicationId;
    
    /**
     * 批次ID
     */
    private String batchId;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 国家代码
     */
    private String countryCode;
    
    /**
     * 运营商
     */
    private String operator;
    
    /**
     * 是否需要状态报告
     */
    private Boolean needStatusReport;
    
    /**
     * 过期时间
     */
    private Long expireTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extendedFields;

    /**
     * 消息ID（用于兼容性）
     */
    private String messageId;

    /**
     * 引用ID（用于兼容性）
     */
    private String referenceId;

    // ========== 兼容性方法 ==========

    /**
     * 获取单个接收号码（兼容旧版本）
     */
    public String getTo() {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            return phoneNumbers.get(0);
        }
        return null;
    }

    /**
     * 设置单个接收号码（兼容旧版本）
     */
    public void setTo(String phoneNumber) {
        if (TextUtils.hasText(phoneNumber)) {
            this.phoneNumbers = Collections.singletonList(phoneNumber);
        }
    }

    /**
     * 获取应用ID（兼容旧版本）
     */
    public String getAppId() {
        return this.applicationId;
    }

    /**
     * 设置应用ID（兼容旧版本）
     */
    public void setAppId(String appId) {
        this.applicationId = appId;
    }

    /**
     * 设置消息ID
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取引用ID
     */
    public String getReferenceId() {
        return this.referenceId;
    }

    /**
     * 设置引用ID
     */
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * 兼容旧版本的构造函数
     */
    public SmsUnifiedRequest(String phoneNumber, String content, String appId) {
        this.phoneNumbers = Collections.singletonList(phoneNumber);
        this.content = content;
        this.applicationId = appId;
    }
} 