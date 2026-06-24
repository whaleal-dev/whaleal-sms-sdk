package com.whaleal.ark.cloud.third.sms.dto;

import java.io.Serializable;

/**
 * SMS统一发送响应DTO
 * 
 * 标准化的短信发送响应格式，包含：
 * 1. 发送结果状态
 * 2. 消息追踪信息
 * 3. 成本和计费信息
 * 4. 错误详情
 */
public class SmsUnifiedResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    // === 基本响应信息 ===
    private String code;                // 响应码（0000-成功，其他-失败）
    private String message;             // 响应消息
    private boolean success;            // 是否成功

    // === 消息追踪信息 ===
    private String messageId;           // 系统消息ID
    private String providerMessageId;   // 服务商消息ID
    private String referenceId;         // 业务引用ID
    private String batchId;             // 批次ID（批量发送时）

    // === 目标信息 ===
    private String to;                  // 目标手机号
    private String from;                // 发送方号码
    private String appId;               // 应用ID

    // === 时间信息 ===
    private Long submitTime;            // 提交时间戳
    private Long processTime;           // 处理时间戳
    private Long deliveryTime;          // 投递时间戳（可选）

    // === 状态信息 ===
    private String status;              // 发送状态
    private String statusMessage;       // 状态描述
    private String providerStatus;      // 服务商状态
    private String providerMessage;     // 服务商消息

    // === 成本信息 ===
    private Double cost;                // 发送成本
    private String currency;            // 货币单位
    private Integer segments;           // 短信段数

    // === 错误信息 ===
    private String errorCode;           // 错误码
    private String errorMessage;        // 错误描述
    private String errorDetail;         // 错误详情

    // === 扩展信息 ===
    private String provider;            // 服务商名称
    private String channel;             // 发送通道
    private String region;              // 地区
    private Integer countryCode;        // 国家代码

    // === 构造函数 ===
    public SmsUnifiedResponse() {}

    public SmsUnifiedResponse(String code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    // === 静态构建方法 ===
    public static SmsUnifiedResponse success() {
        return new SmsUnifiedResponse("0000", "发送成功", true);
    }

    public static SmsUnifiedResponse success(String messageId) {
        SmsUnifiedResponse response = success();
        response.setMessageId(messageId);
        return response;
    }

    public static SmsUnifiedResponse failure(String errorCode, String errorMessage) {
        SmsUnifiedResponse response = new SmsUnifiedResponse(errorCode, errorMessage, false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public static SmsUnifiedResponse failure(String errorMessage) {
        return failure("9999", errorMessage);
    }

    // === 链式设置方法 ===
    public SmsUnifiedResponse messageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public SmsUnifiedResponse providerMessageId(String providerMessageId) {
        this.providerMessageId = providerMessageId;
        return this;
    }

    public SmsUnifiedResponse referenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public SmsUnifiedResponse to(String to) {
        this.to = to;
        return this;
    }

    public SmsUnifiedResponse status(String status) {
        this.status = status;
        return this;
    }

    public SmsUnifiedResponse cost(Double cost, String currency) {
        this.cost = cost;
        this.currency = currency;
        return this;
    }

    public SmsUnifiedResponse provider(String provider) {
        this.provider = provider;
        return this;
    }

    // === Getters and Setters ===
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getProviderMessageId() { return providerMessageId; }
    public void setProviderMessageId(String providerMessageId) { this.providerMessageId = providerMessageId; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public Long getSubmitTime() { return submitTime; }
    public void setSubmitTime(Long submitTime) { this.submitTime = submitTime; }

    public Long getProcessTime() { return processTime; }
    public void setProcessTime(Long processTime) { this.processTime = processTime; }

    public Long getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Long deliveryTime) { this.deliveryTime = deliveryTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getProviderStatus() { return providerStatus; }
    public void setProviderStatus(String providerStatus) { this.providerStatus = providerStatus; }

    public String getProviderMessage() { return providerMessage; }
    public void setProviderMessage(String providerMessage) { this.providerMessage = providerMessage; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getSegments() { return segments; }
    public void setSegments(Integer segments) { this.segments = segments; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getErrorDetail() { return errorDetail; }
    public void setErrorDetail(String errorDetail) { this.errorDetail = errorDetail; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Integer getCountryCode() { return countryCode; }
    public void setCountryCode(Integer countryCode) { this.countryCode = countryCode; }

    @Override
    public String toString() {
        return "SmsUnifiedResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", messageId='" + messageId + '\'' +
                ", to='" + to + '\'' +
                ", status='" + status + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
} 