package com.whaleal.ark.cloud.third.sms.inbound.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInboundMessage;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Vonage上行短信解析器
 */
public class VonageInboundParser implements InboundParser {
    
    @Override
    public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
        return SmsInboundMessage.builder()
                .messageId(getString(rawData, "messageId"))
                .from(getString(rawData, "msisdn"))
                .to(getString(rawData, "to"))
                .content(getString(rawData, "text"))
                .messageType(SmsInboundMessage.MessageType.TEXT)
                .receivedTime(LocalDateTime.now())
                .rawData(rawData)
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