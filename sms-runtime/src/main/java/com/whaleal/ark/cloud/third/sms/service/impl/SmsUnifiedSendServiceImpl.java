package com.whaleal.ark.cloud.third.sms.service.impl;

import com.whaleal.ark.cloud.third.sms.dto.*;
import com.whaleal.ark.cloud.third.sms.service.SmsUnifiedSendService;
import lombok.extern.slf4j.Slf4j;
import com.whaleal.ark.cloud.third.sms.util.TextUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * SMS统一发送服务实现 - SDK层
 * 
 * 提供基础的SMS发送功能实现：
 * 1. 请求验证和预处理
 * 2. 消息ID生成和管理
 * 3. 异步处理支持
 * 4. 状态跟踪和管理
 * 5. 标准化错误处理
 */
/**
 * @deprecated 请使用 {@link com.whaleal.ark.cloud.third.sms.client.DefaultSmsClient}
 */
@Deprecated(since = "1.0.0", forRemoval = true)
@Slf4j
public class SmsUnifiedSendServiceImpl implements SmsUnifiedSendService {

    // 异步执行器
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(10, r -> {
        Thread thread = new Thread(r, "sms-sdk-async-" + System.currentTimeMillis());
        thread.setDaemon(true);
        return thread;
    });

    // 消息状态存储（实际项目中应使用Redis或数据库）
    private final ConcurrentHashMap<String, SmsStatusResponse> messageStatus = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SmsBatchStatusResponse> batchStatus = new ConcurrentHashMap<>();

    @Override
    public SmsUnifiedResponse sendSingle(SmsUnifiedRequest request) {
        try {
            log.info("SDK处理单条短信发送请求：{}", request);

            // 1. 验证请求
            SmsValidationResult validation = validateRequest(request);
            if (!validation.isValid()) {
                return SmsUnifiedResponse.failure(validation.getErrorCode(), validation.getErrorMessage());
            }

            // 2. 生成消息ID
            String messageId = generateMessageId();
            request.setMessageId(messageId);

            // 3. 构建响应
            SmsUnifiedResponse response = SmsUnifiedResponse.success(messageId)
                .to(request.getTo())
                .referenceId(request.getReferenceId());

            // 4. 设置时间戳
            long now = System.currentTimeMillis();
            response.setSubmitTime(now);
            response.setProcessTime(now);

            // 5. 初始化状态跟踪
            initializeMessageStatus(request, response);

            log.info("SDK单条短信发送请求处理完成：messageId={}, to={}", messageId, request.getTo());
            return response;

        } catch (Exception e) {
            log.error("SDK处理单条短信发送请求失败：{}", request.getTo(), e);
            return SmsUnifiedResponse.failure("SDK异常：" + e.getMessage());
        }
    }

    @Override
    public SmsBatchResponse sendBatch(SmsBatchRequest request) {
        try {
            log.info("SDK处理批量短信发送请求：{}", request);

            // 1. 验证批量请求
            if (!request.isValid()) {
                return SmsBatchResponse.failure("批量请求验证失败：缺少必要参数");
            }

            // 2. 生成批次ID
            String batchId = generateBatchId();
            request.setBatchId(batchId);

            // 3. 处理批量消息
            List<SmsUnifiedResponse> results = processBatchMessages(request);

            // 4. 构建批量响应
            SmsBatchResponse response = SmsBatchResponse.success(batchId)
                .batchName(request.getBatchName())
                .referenceId(request.getReferenceId())
                .results(results)
                .status("COMPLETED");

            // 5. 计算统计信息
            response.calculateStatistics();
            response.setSubmitTime(System.currentTimeMillis());
            response.setCompleteTime(System.currentTimeMillis());
            response.calculateDuration();

            // 6. 初始化批次状态跟踪
            initializeBatchStatus(request, response);

            log.info("SDK批量短信发送请求处理完成：batchId={}, totalCount={}, successCount={}", 
                batchId, response.getTotalCount(), response.getSuccessCount());
            return response;

        } catch (Exception e) {
            log.error("SDK处理批量短信发送请求失败", e);
            return SmsBatchResponse.failure("SDK异常：" + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<SmsUnifiedResponse> sendSingleAsync(SmsUnifiedRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendSingle(request);
            } catch (Exception e) {
                log.error("SDK异步处理单条短信发送失败：{}", request.getTo(), e);
                return SmsUnifiedResponse.failure("异步处理异常：" + e.getMessage());
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<SmsBatchResponse> sendBatchAsync(SmsBatchRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendBatch(request);
            } catch (Exception e) {
                log.error("SDK异步处理批量短信发送失败", e);
                return SmsBatchResponse.failure("异步处理异常：" + e.getMessage());
            }
        }, asyncExecutor);
    }

    @Override
    public SmsValidationResult validateRequest(SmsUnifiedRequest request) {
        if (request == null) {
            return SmsValidationResult.failure("请求不能为空", "E001");
        }

        if (!TextUtils.hasText(request.getTo())) {
            return SmsValidationResult.failure("目标手机号不能为空", "E002");
        }

        if (!TextUtils.hasText(request.getContent()) && !TextUtils.hasText(request.getTemplateId())) {
            return SmsValidationResult.failure("发送内容或模板ID不能同时为空", "E003");
        }

        if (!TextUtils.hasText(request.getApplicationId())) {
            return SmsValidationResult.failure("应用ID不能为空", "E004");
        }

        // 验证手机号格式（简单验证）
        if (!isValidPhoneNumber(request.getTo())) {
            return SmsValidationResult.failure("手机号格式不正确", "E005");
        }

        // 验证内容长度
        if (TextUtils.hasText(request.getContent()) && request.getContent().length() > 1000) {
            return SmsValidationResult.failure("发送内容超过最大长度限制", "E006");
        }

        return SmsValidationResult.success();
    }

    @Override
    public SmsStatusResponse getStatus(String messageId) {
        SmsStatusResponse status = messageStatus.get(messageId);
        if (status == null) {
            status = new SmsStatusResponse();
            status.setMessageId(messageId);
            status.setStatus("NOT_FOUND");
            status.setStatusMessage("消息未找到");
        }
        return status;
    }

    @Override
    public SmsBatchStatusResponse getBatchStatus(String batchId) {
        SmsBatchStatusResponse status = batchStatus.get(batchId);
        if (status == null) {
            status = new SmsBatchStatusResponse();
            status.setBatchId(batchId);
            status.setStatus("NOT_FOUND");
        }
        return status;
    }

    @Override
    public boolean cancelSending(String messageId) {
        try {
            // 更新消息状态为取消
            SmsStatusResponse status = messageStatus.get(messageId);
            if (status != null) {
                status.setStatus("CANCELLED");
                status.setStatusMessage("发送已取消");
                status.setTimestamp(System.currentTimeMillis());
                log.info("SDK取消消息发送：messageId={}", messageId);
                return true;
            }

            // 尝试取消批次
            SmsBatchStatusResponse batchStatusResp = batchStatus.get(messageId);
            if (batchStatusResp != null) {
                batchStatusResp.setStatus("CANCELLED");
                log.info("SDK取消批次发送：batchId={}", messageId);
                return true;
            }

            log.warn("SDK未找到要取消的消息或批次：{}", messageId);
            return false;

        } catch (Exception e) {
            log.error("SDK取消发送失败：messageId={}", messageId, e);
            return false;
        }
    }

    // ========== 私有方法 ==========

    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return "MSG_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 生成批次ID
     */
    private String generateBatchId() {
        return "BATCH_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 验证手机号格式
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (!TextUtils.hasText(phoneNumber)) {
            return false;
        }
        // 简单的手机号验证（实际项目中应该更严格）
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");
        return cleaned.length() >= 10 && cleaned.length() <= 15;
    }

    /**
     * 处理批量消息
     */
    private List<SmsUnifiedResponse> processBatchMessages(SmsBatchRequest request) {
        List<SmsUnifiedResponse> results = new ArrayList<>();

        if (request.isUseUnifiedContent()) {
            // 统一内容模式
            for (String phoneNumber : request.getToList()) {
                SmsUnifiedRequest singleRequest = new SmsUnifiedRequest(phoneNumber, request.getContent(), request.getAppId());
                singleRequest.setTemplateId(request.getTemplateId());
                singleRequest.setTemplateParams(request.getCommonTemplateParams());
                singleRequest.setSignature(request.getSignature());
                singleRequest.setPriority(request.getPriority());
                singleRequest.setReferenceId(request.getReferenceId());

                SmsUnifiedResponse singleResponse = sendSingle(singleRequest);
                results.add(singleResponse);
            }
        } else {
            // 个性化消息模式
            for (SmsUnifiedRequest message : request.getMessages()) {
                // 设置批量请求中的公共参数
                if (!TextUtils.hasText(message.getApplicationId())) {
                    message.setApplicationId(request.getAppId());
                }
                if (!TextUtils.hasText(message.getSignature()) && TextUtils.hasText(request.getSignature())) {
                    message.setSignature(request.getSignature());
                }
                if (message.getPriority() == null && request.getPriority() != null) {
                    message.setPriority(request.getPriority());
                }

                SmsUnifiedResponse singleResponse = sendSingle(message);
                results.add(singleResponse);
            }
        }

        return results;
    }

    /**
     * 初始化消息状态跟踪
     */
    private void initializeMessageStatus(SmsUnifiedRequest request, SmsUnifiedResponse response) {
        SmsStatusResponse status = new SmsStatusResponse();
        status.setMessageId(response.getMessageId());
        status.setStatus("SUBMITTED");
        status.setStatusMessage("消息已提交");
        status.setTimestamp(System.currentTimeMillis());

        messageStatus.put(response.getMessageId(), status);
    }

    /**
     * 初始化批次状态跟踪
     */
    private void initializeBatchStatus(SmsBatchRequest request, SmsBatchResponse response) {
        SmsBatchStatusResponse status = new SmsBatchStatusResponse();
        status.setBatchId(response.getBatchId());
        status.setStatus(response.getStatus());
        status.setTotalCount(response.getTotalCount());
        status.setSentCount(response.getTotalCount());
        status.setSuccessCount(response.getSuccessCount());
        status.setFailedCount(response.getFailedCount());
        status.setPendingCount(response.getPendingCount());

        // 设置详细状态
        if (response.getResults() != null) {
            List<SmsStatusResponse> details = response.getResults().stream()
                .map(this::convertToStatusResponse)
                .collect(Collectors.toList());
            status.setDetails(details);
        }

        batchStatus.put(response.getBatchId(), status);
    }

    /**
     * 转换为状态响应
     */
    private SmsStatusResponse convertToStatusResponse(SmsUnifiedResponse response) {
        SmsStatusResponse status = new SmsStatusResponse();
        status.setMessageId(response.getMessageId());
        status.setStatus(response.isSuccess() ? "SUCCESS" : "FAILED");
        status.setStatusMessage(response.getMessage());
        status.setProviderMessageId(response.getProviderMessageId());
        status.setTimestamp(response.getProcessTime());
        return status;
    }
} 