package com.whaleal.ark.cloud.third.sms.receipt.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.receipt.entity.SmsReceipt;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Vonage回执解析器
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class VonageReceiptParser implements ReceiptParser {
    
    @Override
    public SmsReceipt parse(Map<String, Object> rawData, SmsProviderConfig config) {
        if (!isValidData(rawData)) {
            throw new IllegalArgumentException("无效的Vonage回执数据");
        }
        
        return SmsReceipt.builder()
                .receiptId(getString(rawData, "messageId"))
                .messageId(getString(rawData, "messageId"))
                .to(getString(rawData, "msisdn"))
                .receiptStatus(parseStatus(getString(rawData, "status")))
                .receiptCode(getString(rawData, "status"))
                .receiptDescription(getString(rawData, "err-code"))
                .errorCode(getString(rawData, "err-code"))
                .deliveredTime(parseDateTime(getString(rawData, "message-timestamp")))
                .receivedTime(LocalDateTime.now())
                .costInfo(parseCostInfo(rawData))
                .networkInfo(parseNetworkInfo(rawData))
                .rawData(rawData)
                .build();
    }
    
    private SmsReceipt.ReceiptStatus parseStatus(String status) {
        if (status == null) return SmsReceipt.ReceiptStatus.UNKNOWN;
        
        switch (status.toLowerCase()) {
            case "delivered":
                return SmsReceipt.ReceiptStatus.DELIVERED;
            case "failed":
                return SmsReceipt.ReceiptStatus.FAILED;
            case "expired":
                return SmsReceipt.ReceiptStatus.EXPIRED;
            case "rejected":
                return SmsReceipt.ReceiptStatus.REJECTED;
            case "undeliverable":
                return SmsReceipt.ReceiptStatus.UNDELIVERABLE;
            default:
                return SmsReceipt.ReceiptStatus.UNKNOWN;
        }
    }
    
    private LocalDateTime parseDateTime(String timestamp) {
        if (timestamp == null) return null;
        
        try {
            // Vonage时间格式: 2020-01-01 12:00:00
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析Vonage时间失败: {}", timestamp);
            return null;
        }
    }
    
    private SmsReceipt.CostInfo parseCostInfo(Map<String, Object> rawData) {
        return SmsReceipt.CostInfo.builder()
                .amount(getString(rawData, "price"))
                .currency(getString(rawData, "currency"))
                .billingType("per_message")
                .messageCount(1)
                .build();
    }
    
    private SmsReceipt.NetworkInfo parseNetworkInfo(Map<String, Object> rawData) {
        return SmsReceipt.NetworkInfo.builder()
                .carrierName(getString(rawData, "network-code"))
                .carrierCode(getString(rawData, "network-code"))
                .countryCode(getString(rawData, "to"))
                .networkType("SMS")
                .build();
    }
    
    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
    
    @Override
    public String getSupportedProvider() {
        return "VONAGE";
    }
} 