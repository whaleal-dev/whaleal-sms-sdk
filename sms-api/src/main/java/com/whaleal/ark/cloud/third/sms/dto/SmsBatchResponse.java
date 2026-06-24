package com.whaleal.ark.cloud.third.sms.dto;

import java.io.Serializable;
import java.util.List;

/**
 * SMS批量发送响应DTO
 * 
 * 批量发送结果：
 * 1. 整体结果状态
 * 2. 每条消息的详细结果
 * 3. 统计信息
 * 4. 批次跟踪信息
 */
public class SmsBatchResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    // === 基本响应信息 ===
    private String code;                // 响应码（0000-成功，其他-失败）
    private String message;             // 响应消息
    private boolean success;            // 整体是否成功

    // === 批次信息 ===
    private String batchId;             // 批次ID
    private String batchName;           // 批次名称
    private String referenceId;         // 业务引用ID

    // === 统计信息 ===
    private Integer totalCount;         // 总数量
    private Integer successCount;       // 成功数量
    private Integer failedCount;        // 失败数量
    private Integer pendingCount;       // 待处理数量
    private Double successRate;         // 成功率

    // === 时间信息 ===
    private Long submitTime;            // 提交时间戳
    private Long startTime;             // 开始处理时间戳
    private Long completeTime;          // 完成时间戳
    private Long duration;              // 处理耗时（毫秒）

    // === 详细结果 ===
    private List<SmsUnifiedResponse> results; // 每条消息的详细结果
    private List<String> failedNumbers;       // 失败的号码列表
    private List<String> successNumbers;      // 成功的号码列表

    // === 成本信息 ===
    private Double totalCost;           // 总成本
    private String currency;            // 货币单位
    private Integer totalSegments;      // 总短信段数

    // === 状态信息 ===
    private String status;              // 批次状态（PENDING/PROCESSING/COMPLETED/FAILED/CANCELLED）
    private String statusMessage;       // 状态描述

    // === 错误信息 ===
    private String errorCode;           // 错误码
    private String errorMessage;        // 错误描述
    private List<String> errors;        // 错误列表

    // === 扩展信息 ===
    private String appId;               // 应用ID
    private String provider;            // 服务商名称
    private String channel;             // 发送通道

    // === 构造函数 ===
    public SmsBatchResponse() {}

    public SmsBatchResponse(String code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    // === 静态构建方法 ===
    public static SmsBatchResponse success() {
        return new SmsBatchResponse("0000", "批量发送成功", true);
    }

    public static SmsBatchResponse success(String batchId) {
        SmsBatchResponse response = success();
        response.setBatchId(batchId);
        return response;
    }

    public static SmsBatchResponse failure(String errorCode, String errorMessage) {
        SmsBatchResponse response = new SmsBatchResponse(errorCode, errorMessage, false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public static SmsBatchResponse failure(String errorMessage) {
        return failure("9999", errorMessage);
    }

    // === 链式设置方法 ===
    public SmsBatchResponse batchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public SmsBatchResponse batchName(String batchName) {
        this.batchName = batchName;
        return this;
    }

    public SmsBatchResponse referenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public SmsBatchResponse statistics(Integer total, Integer success, Integer failed) {
        this.totalCount = total;
        this.successCount = success;
        this.failedCount = failed;
        this.pendingCount = total - success - failed;
        this.successRate = total > 0 ? (success * 100.0 / total) : 0.0;
        return this;
    }

    public SmsBatchResponse status(String status) {
        this.status = status;
        return this;
    }

    public SmsBatchResponse results(List<SmsUnifiedResponse> results) {
        this.results = results;
        return this;
    }

    public SmsBatchResponse cost(Double totalCost, String currency) {
        this.totalCost = totalCost;
        this.currency = currency;
        return this;
    }

    // === 计算方法 ===
    public void calculateStatistics() {
        if (results != null && !results.isEmpty()) {
            this.totalCount = results.size();
            this.successCount = (int) results.stream().filter(SmsUnifiedResponse::isSuccess).count();
            this.failedCount = totalCount - successCount;
            this.pendingCount = 0;
            this.successRate = totalCount > 0 ? (successCount * 100.0 / totalCount) : 0.0;
            
            // 计算总成本
            this.totalCost = results.stream()
                .filter(r -> r.getCost() != null)
                .mapToDouble(SmsUnifiedResponse::getCost)
                .sum();
                
            // 计算总段数
            this.totalSegments = results.stream()
                .filter(r -> r.getSegments() != null)
                .mapToInt(SmsUnifiedResponse::getSegments)
                .sum();
        }
    }

    public void calculateDuration() {
        if (startTime != null && completeTime != null) {
            this.duration = completeTime - startTime;
        }
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status);
    }

    // === Getters and Setters ===
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

    public Integer getFailedCount() { return failedCount; }
    public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }

    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }

    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }

    public Long getSubmitTime() { return submitTime; }
    public void setSubmitTime(Long submitTime) { this.submitTime = submitTime; }

    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }

    public Long getCompleteTime() { return completeTime; }
    public void setCompleteTime(Long completeTime) { this.completeTime = completeTime; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public List<SmsUnifiedResponse> getResults() { return results; }
    public void setResults(List<SmsUnifiedResponse> results) { this.results = results; }

    public List<String> getFailedNumbers() { return failedNumbers; }
    public void setFailedNumbers(List<String> failedNumbers) { this.failedNumbers = failedNumbers; }

    public List<String> getSuccessNumbers() { return successNumbers; }
    public void setSuccessNumbers(List<String> successNumbers) { this.successNumbers = successNumbers; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getTotalSegments() { return totalSegments; }
    public void setTotalSegments(Integer totalSegments) { this.totalSegments = totalSegments; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    @Override
    public String toString() {
        return "SmsBatchResponse{" +
                "code='" + code + '\'' +
                ", success=" + success +
                ", batchId='" + batchId + '\'' +
                ", totalCount=" + totalCount +
                ", successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", successRate=" + successRate +
                ", status='" + status + '\'' +
                '}';
    }
} 