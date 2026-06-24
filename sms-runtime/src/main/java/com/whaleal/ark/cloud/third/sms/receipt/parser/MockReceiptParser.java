package com.whaleal.ark.cloud.third.sms.receipt.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.receipt.entity.SmsReceipt;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

/**
 * Mock测试平台回执解析器
 * 用于测试和开发环境的模拟回执解析
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class MockReceiptParser implements ReceiptParser {
    
    private final Random random = new Random();
    
    @Override
    public SmsReceipt parse(Map<String, Object> rawData, SmsProviderConfig config) {
        if (!isValidData(rawData)) {
            throw new IllegalArgumentException("无效的Mock回执数据");
        }
        
        log.debug("Mock解析回执数据: {}", rawData);
        
        // 模拟不同的回执状态
        SmsReceipt.ReceiptStatus status = simulateReceiptStatus();
        
        return SmsReceipt.builder()
                .receiptId(getString(rawData, "receiptId", "mock_receipt_" + System.currentTimeMillis()))
                .messageId(getString(rawData, "messageId", "mock_msg_" + System.currentTimeMillis()))
                .to(getString(rawData, "to", "+86138****8888"))
                .receiptStatus(status)
                .receiptCode(status.getCode())
                .receiptDescription("Mock模拟回执: " + status.getDescription())
                .deliveredTime(status == SmsReceipt.ReceiptStatus.DELIVERED ? LocalDateTime.now() : null)
                .receivedTime(LocalDateTime.now())
                .costInfo(createMockCostInfo())
                .networkInfo(createMockNetworkInfo())
                .rawData(rawData)
                .build();
    }
    
    /**
     * 模拟回执状态
     * 80%概率成功，10%失败，5%过期，5%拒绝
     */
    private SmsReceipt.ReceiptStatus simulateReceiptStatus() {
        int probability = random.nextInt(100);
        
        if (probability < 80) {
            return SmsReceipt.ReceiptStatus.DELIVERED;
        } else if (probability < 90) {
            return SmsReceipt.ReceiptStatus.FAILED;
        } else if (probability < 95) {
            return SmsReceipt.ReceiptStatus.EXPIRED;
        } else {
            return SmsReceipt.ReceiptStatus.REJECTED;
        }
    }
    
    /**
     * 创建模拟费用信息
     */
    private SmsReceipt.CostInfo createMockCostInfo() {
        // 模拟不同的费用
        String[] amounts = {"0.045", "0.050", "0.055", "0.060"};
        String[] currencies = {"CNY", "USD", "EUR"};
        
        return SmsReceipt.CostInfo.builder()
                .amount(amounts[random.nextInt(amounts.length)])
                .currency(currencies[random.nextInt(currencies.length)])
                .billingType("per_message")
                .messageCount(1)
                .build();
    }
    
    /**
     * 创建模拟网络信息
     */
    private SmsReceipt.NetworkInfo createMockNetworkInfo() {
        String[] carriers = {"中国移动", "中国联通", "中国电信", "Verizon", "AT&T", "T-Mobile"};
        String[] countryCodes = {"CN", "US", "GB", "DE", "FR", "JP"};
        
        return SmsReceipt.NetworkInfo.builder()
                .carrierName(carriers[random.nextInt(carriers.length)])
                .carrierCode("MOCK_" + random.nextInt(1000))
                .countryCode(countryCodes[random.nextInt(countryCodes.length)])
                .networkType("SMS")
                .build();
    }
    
    /**
     * 获取字符串值，支持默认值
     */
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    @Override
    public String getSupportedProvider() {
        return "MOCK";
    }
} 