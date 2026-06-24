package com.whaleal.ark.cloud.third.sms.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SMS批量发送请求DTO
 * 
 * 支持批量发送短信：
 * 1. 多个目标号码
 * 2. 统一或个性化内容
 * 3. 批次管理
 * 4. 发送策略配置
 */
public class SmsBatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // === 批次信息 ===
    private String batchId;             // 批次ID（可选，系统自动生成）
    private String batchName;           // 批次名称
    private String description;         // 批次描述

    // === 发送内容 ===
    private List<String> toList;        // 目标手机号列表
    private String content;             // 统一发送内容（当useUnifiedContent=true时使用）
    private List<SmsUnifiedRequest> messages; // 个性化消息列表（当useUnifiedContent=false时使用）
    private boolean useUnifiedContent = true; // 是否使用统一内容

    // === 基本信息 ===
    private String from;                // 发送方号码
    private String appId;               // 应用ID
    private Long userId;                // 用户ID

    // === 模板相关 ===
    private String templateId;          // 模板ID
    private String templateType;        // 模板类型
    private Map<String, Object> commonTemplateParams; // 公共模板参数

    // === 签名相关 ===
    private String signature;           // 签名
    private String signatureId;         // 签名ID

    // === 发送策略 ===
    private Integer priority;           // 优先级（1-高，2-中，3-低）
    private Long scheduledTime;         // 定时发送时间戳
    private Integer batchSize;          // 批次大小（每批发送数量）
    private Integer intervalSeconds;    // 批次间隔（秒）
    private Integer retryCount;         // 重试次数
    private Integer timeout;            // 超时时间（秒）
    private Integer concurrency;        // 并发数量

    // === 扩展属性 ===
    private String referenceId;         // 业务引用ID
    private String callbackUrl;         // 回调URL
    private Map<String, String> extraParams; // 扩展参数
    private String tag;                 // 标签

    // === 构造函数 ===
    public SmsBatchRequest() {}

    public SmsBatchRequest(List<String> toList, String content, String appId) {
        this.toList = toList;
        this.content = content;
        this.appId = appId;
        this.useUnifiedContent = true;
    }

    public SmsBatchRequest(List<SmsUnifiedRequest> messages, String appId) {
        this.messages = messages;
        this.appId = appId;
        this.useUnifiedContent = false;
    }

    // === 静态构建方法 ===
    public static SmsBatchRequest unifiedContent(List<String> toList, String content, String appId) {
        return new SmsBatchRequest(toList, content, appId);
    }

    public static SmsBatchRequest personalizedMessages(List<SmsUnifiedRequest> messages, String appId) {
        return new SmsBatchRequest(messages, appId);
    }

    // === 链式设置方法 ===
    public SmsBatchRequest batchName(String batchName) {
        this.batchName = batchName;
        return this;
    }

    public SmsBatchRequest template(String templateId, Map<String, Object> params) {
        this.templateId = templateId;
        this.commonTemplateParams = params;
        return this;
    }

    public SmsBatchRequest signature(String signature) {
        this.signature = signature;
        return this;
    }

    public SmsBatchRequest priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public SmsBatchRequest scheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
        return this;
    }

    public SmsBatchRequest batchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public SmsBatchRequest interval(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        return this;
    }

    public SmsBatchRequest concurrency(Integer concurrency) {
        this.concurrency = concurrency;
        return this;
    }

    public SmsBatchRequest referenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public SmsBatchRequest callbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
        return this;
    }

    // === 验证方法 ===
    public boolean isValid() {
        if (appId == null || appId.trim().isEmpty()) {
            return false;
        }
        
        if (useUnifiedContent) {
            return toList != null && !toList.isEmpty() && 
                   content != null && !content.trim().isEmpty();
        } else {
            return messages != null && !messages.isEmpty();
        }
    }

    public int getTotalCount() {
        if (useUnifiedContent) {
            return toList != null ? toList.size() : 0;
        } else {
            return messages != null ? messages.size() : 0;
        }
    }

    // === Getters and Setters ===
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getToList() { return toList; }
    public void setToList(List<String> toList) { this.toList = toList; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<SmsUnifiedRequest> getMessages() { return messages; }
    public void setMessages(List<SmsUnifiedRequest> messages) { this.messages = messages; }

    public boolean isUseUnifiedContent() { return useUnifiedContent; }
    public void setUseUnifiedContent(boolean useUnifiedContent) { this.useUnifiedContent = useUnifiedContent; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public Map<String, Object> getCommonTemplateParams() { return commonTemplateParams; }
    public void setCommonTemplateParams(Map<String, Object> commonTemplateParams) { this.commonTemplateParams = commonTemplateParams; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getSignatureId() { return signatureId; }
    public void setSignatureId(String signatureId) { this.signatureId = signatureId; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Long getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(Long scheduledTime) { this.scheduledTime = scheduledTime; }

    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }

    public Integer getIntervalSeconds() { return intervalSeconds; }
    public void setIntervalSeconds(Integer intervalSeconds) { this.intervalSeconds = intervalSeconds; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }

    public Integer getConcurrency() { return concurrency; }
    public void setConcurrency(Integer concurrency) { this.concurrency = concurrency; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public Map<String, String> getExtraParams() { return extraParams; }
    public void setExtraParams(Map<String, String> extraParams) { this.extraParams = extraParams; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    @Override
    public String toString() {
        return "SmsBatchRequest{" +
                "batchId='" + batchId + '\'' +
                ", batchName='" + batchName + '\'' +
                ", totalCount=" + getTotalCount() +
                ", appId='" + appId + '\'' +
                ", useUnifiedContent=" + useUnifiedContent +
                ", priority=" + priority +
                '}';
    }
} 