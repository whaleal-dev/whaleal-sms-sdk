package com.whaleal.ark.cloud.third.sms.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 批量发送结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsBatchSendResult {

    private boolean success;
    private SmsSendMode mode;
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<SmsSendResult> results;

    public static SmsBatchSendResult of(List<SmsSendResult> results, SmsSendMode mode) {
        if (results == null || results.isEmpty()) {
            return SmsBatchSendResult.builder()
                    .success(false)
                    .mode(mode)
                    .totalCount(0)
                    .successCount(0)
                    .failureCount(0)
                    .results(Collections.emptyList())
                    .build();
        }
        int successCount = (int) results.stream().filter(SmsSendResult::isSuccess).count();
        return SmsBatchSendResult.builder()
                .mode(mode)
                .results(results)
                .totalCount(results.size())
                .successCount(successCount)
                .failureCount(results.size() - successCount)
                .success(successCount == results.size())
                .build();
    }

    public static SmsBatchSendResult failure(SmsSendMode mode, String errorCode, String errorMessage) {
        return SmsBatchSendResult.builder()
                .success(false)
                .mode(mode)
                .totalCount(0)
                .successCount(0)
                .failureCount(0)
                .results(List.of(SmsSendResult.failure(null, errorCode, errorMessage)))
                .build();
    }
}
