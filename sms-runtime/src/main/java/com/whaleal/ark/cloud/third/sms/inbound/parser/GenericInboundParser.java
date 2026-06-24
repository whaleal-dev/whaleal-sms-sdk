package com.whaleal.ark.cloud.third.sms.inbound.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInboundMessage;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通用上行短信解析器 - 兜底解析器
 */
public class GenericInboundParser implements InboundParser {
    
    @Override
    public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
        return SmsInboundMessage.builder()
                .messageId(findValue(rawData, "id", "messageId", "msgId"))
                .from(findValue(rawData, "from", "phone", "mobile", "msisdn"))
                .to(findValue(rawData, "to", "dest", "destination"))
                .content(findValue(rawData, "content", "text", "message", "body"))
                .messageType(SmsInboundMessage.MessageType.TEXT)
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
    
    @Override
    public String getSupportedProvider() {
        return "GENERIC";
    }
} 