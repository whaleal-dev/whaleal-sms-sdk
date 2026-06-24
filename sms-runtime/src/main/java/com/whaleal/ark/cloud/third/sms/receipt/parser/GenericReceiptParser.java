package com.whaleal.ark.cloud.third.sms.receipt.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.receipt.entity.SmsReceipt;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通用回执解析器 - 兜底解析器
 */
public class GenericReceiptParser implements ReceiptParser {
    
    @Override
    public SmsReceipt parse(Map<String, Object> rawData, SmsProviderConfig config) {
        return SmsReceipt.builder()
                .receiptId(findValue(rawData, "id", "messageId", "receiptId"))
                .messageId(findValue(rawData, "messageId", "id", "msgId"))
                .to(findValue(rawData, "to", "phone", "mobile"))
                .receiptStatus(parseGenericStatus(findValue(rawData, "status", "state", "result")))
                .receiptCode(findValue(rawData, "status", "code", "result"))
                .receivedTime(LocalDateTime.now())
                .rawData(rawData)
                .build();
    }
    
    private String findValue(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
    
    private SmsReceipt.ReceiptStatus parseGenericStatus(String status) {
        if (status == null) return SmsReceipt.ReceiptStatus.UNKNOWN;
        
        String lowerStatus = status.toLowerCase();
        if (lowerStatus.contains("deliver") || lowerStatus.contains("success")) {
            return SmsReceipt.ReceiptStatus.DELIVERED;
        } else if (lowerStatus.contains("fail") || lowerStatus.contains("error")) {
            return SmsReceipt.ReceiptStatus.FAILED;
        } else if (lowerStatus.contains("expire")) {
            return SmsReceipt.ReceiptStatus.EXPIRED;
        } else if (lowerStatus.contains("reject")) {
            return SmsReceipt.ReceiptStatus.REJECTED;
        } else {
            return SmsReceipt.ReceiptStatus.UNKNOWN;
        }
    }
    
    @Override
    public String getSupportedProvider() {
        return "GENERIC";
    }
} 