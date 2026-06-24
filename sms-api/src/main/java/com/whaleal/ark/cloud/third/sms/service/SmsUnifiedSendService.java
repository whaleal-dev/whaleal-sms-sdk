package com.whaleal.ark.cloud.third.sms.service;

import com.whaleal.ark.cloud.third.sms.dto.SmsUnifiedRequest;
import com.whaleal.ark.cloud.third.sms.dto.SmsUnifiedResponse;
import com.whaleal.ark.cloud.third.sms.dto.SmsBatchRequest;
import com.whaleal.ark.cloud.third.sms.dto.SmsBatchResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @deprecated 请使用 {@link com.whaleal.ark.cloud.third.sms.client.SmsClient}
 */
@Deprecated(since = "1.0.0", forRemoval = true)
public interface SmsUnifiedSendService {

    /**
     * 发送单条短信
     * 
     * @param request 统一发送请求
     * @return 发送响应
     */
    SmsUnifiedResponse sendSingle(SmsUnifiedRequest request);

    /**
     * 批量发送短信
     * 
     * @param request 批量发送请求
     * @return 批量发送响应
     */
    SmsBatchResponse sendBatch(SmsBatchRequest request);

    /**
     * 异步发送单条短信
     * 
     * @param request 统一发送请求
     * @return 异步发送Future
     */
    CompletableFuture<SmsUnifiedResponse> sendSingleAsync(SmsUnifiedRequest request);

    /**
     * 异步批量发送短信
     * 
     * @param request 批量发送请求
     * @return 异步批量发送Future
     */
    CompletableFuture<SmsBatchResponse> sendBatchAsync(SmsBatchRequest request);

    /**
     * 验证发送请求
     * 
     * @param request 发送请求
     * @return 验证结果
     */
    SmsValidationResult validateRequest(SmsUnifiedRequest request);

    /**
     * 获取发送状态
     * 
     * @param messageId 消息ID
     * @return 发送状态
     */
    SmsStatusResponse getStatus(String messageId);

    /**
     * 获取批量发送状态
     * 
     * @param batchId 批次ID
     * @return 批量发送状态
     */
    SmsBatchStatusResponse getBatchStatus(String batchId);

    /**
     * 取消发送任务
     * 
     * @param messageId 消息ID或批次ID
     * @return 取消结果
     */
    boolean cancelSending(String messageId);

    /**
     * 验证结果
     */
    class SmsValidationResult {
        private boolean valid;
        private String errorMessage;
        private String errorCode;

        public SmsValidationResult() {}

        public SmsValidationResult(boolean valid, String errorMessage, String errorCode) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        public static SmsValidationResult success() {
            return new SmsValidationResult(true, null, null);
        }

        public static SmsValidationResult failure(String errorMessage, String errorCode) {
            return new SmsValidationResult(false, errorMessage, errorCode);
        }

        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    }

    /**
     * 发送状态响应
     */
    class SmsStatusResponse {
        private String messageId;
        private String status;
        private String statusMessage;
        private String providerMessageId;
        private Long timestamp;

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getStatusMessage() { return statusMessage; }
        public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

        public String getProviderMessageId() { return providerMessageId; }
        public void setProviderMessageId(String providerMessageId) { this.providerMessageId = providerMessageId; }

        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 批量发送状态响应
     */
    class SmsBatchStatusResponse {
        private String batchId;
        private String status;
        private Integer totalCount;
        private Integer sentCount;
        private Integer successCount;
        private Integer failedCount;
        private Integer pendingCount;
        private List<SmsStatusResponse> details;

        // Getters and Setters
        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

        public Integer getSentCount() { return sentCount; }
        public void setSentCount(Integer sentCount) { this.sentCount = sentCount; }

        public Integer getSuccessCount() { return successCount; }
        public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

        public Integer getFailedCount() { return failedCount; }
        public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }

        public Integer getPendingCount() { return pendingCount; }
        public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }

        public List<SmsStatusResponse> getDetails() { return details; }
        public void setDetails(List<SmsStatusResponse> details) { this.details = details; }
    }
} 