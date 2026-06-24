package com.whaleal.ark.cloud.third.sms.util;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInboundMessage;
import com.whaleal.ark.cloud.third.sms.inbound.parser.InboundParser;
import com.whaleal.ark.cloud.third.sms.outbound.entity.SmsOutboundMessage;
import com.whaleal.ark.cloud.third.sms.outbound.sender.OutboundSender;
import com.whaleal.ark.cloud.third.sms.report.entity.SmsReport;
import com.whaleal.ark.cloud.third.sms.report.fetcher.ReportFetcher;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 存根实现类 - 提供基础实现以避免编译错误
 * 实际使用时应该替换为具体的提供商实现
 */
public class StubImplementations {
    
    // ============ Inbound Parser 存根实现 ============
    
    public static class TencentInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "TENCENT");
        }
        @Override
        public String getSupportedProvider() { return "TENCENT"; }
    }
    
    public static class HuaweiInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "HUAWEI");
        }
        @Override
        public String getSupportedProvider() { return "HUAWEI"; }
    }
    
    public static class TwilioInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "TWILIO");
        }
        @Override
        public String getSupportedProvider() { return "TWILIO"; }
    }
    
    public static class AwsSnsInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "AWS_SNS");
        }
        @Override
        public String getSupportedProvider() { return "AWS_SNS"; }
    }
    
    public static class ChinaMobileInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "CHINA_MOBILE");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_MOBILE"; }
    }
    
    public static class ChinaTelecomInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "CHINA_TELECOM");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_TELECOM"; }
    }
    
    public static class ChinaUnicomInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "CHINA_UNICOM");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_UNICOM"; }
    }
    
    public static class CustomHttpInboundParser implements InboundParser {
        @Override
        public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
            return createStubInbound(rawData, "CUSTOM_HTTP");
        }
        @Override
        public String getSupportedProvider() { return "CUSTOM_HTTP"; }
    }
    
    // ============ Report Fetcher 存根实现 ============
    
    public static class AliyunReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "ALIYUN");
        }
        @Override
        public String getSupportedProvider() { return "ALIYUN"; }
    }
    
    public static class TencentReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "TENCENT");
        }
        @Override
        public String getSupportedProvider() { return "TENCENT"; }
    }
    
    public static class HuaweiReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "HUAWEI");
        }
        @Override
        public String getSupportedProvider() { return "HUAWEI"; }
    }
    
    public static class TwilioReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "TWILIO");
        }
        @Override
        public String getSupportedProvider() { return "TWILIO"; }
    }
    
    public static class AwsSnsReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "AWS_SNS");
        }
        @Override
        public String getSupportedProvider() { return "AWS_SNS"; }
    }
    
    public static class ChinaMobileReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "CHINA_MOBILE");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_MOBILE"; }
    }
    
    public static class ChinaTelecomReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "CHINA_TELECOM");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_TELECOM"; }
    }
    
    public static class ChinaUnicomReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "CHINA_UNICOM");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_UNICOM"; }
    }
    
    public static class CustomHttpReportFetcher implements ReportFetcher {
        @Override
        public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
            return createStubReport(messageId, "CUSTOM_HTTP");
        }
        @Override
        public String getSupportedProvider() { return "CUSTOM_HTTP"; }
    }
    
    // ============ Outbound Sender 存根实现 ============
    
    public static class AliyunOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "ALIYUN");
        }
        @Override
        public String getSupportedProvider() { return "ALIYUN"; }
    }
    
    public static class TencentOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "TENCENT");
        }
        @Override
        public String getSupportedProvider() { return "TENCENT"; }
    }
    
    public static class HuaweiOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "HUAWEI");
        }
        @Override
        public String getSupportedProvider() { return "HUAWEI"; }
    }
    
    public static class TwilioOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "TWILIO");
        }
        @Override
        public String getSupportedProvider() { return "TWILIO"; }
    }
    
    public static class AwsSnsOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "AWS_SNS");
        }
        @Override
        public String getSupportedProvider() { return "AWS_SNS"; }
    }
    
    public static class ChinaMobileOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "CHINA_MOBILE");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_MOBILE"; }
    }
    
    public static class ChinaTelecomOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "CHINA_TELECOM");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_TELECOM"; }
    }
    
    public static class ChinaUnicomOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "CHINA_UNICOM");
        }
        @Override
        public String getSupportedProvider() { return "CHINA_UNICOM"; }
    }
    
    public static class CustomHttpOutboundSender implements OutboundSender {
        @Override
        public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
            return createStubOutbound(message, "CUSTOM_HTTP");
        }
        @Override
        public String getSupportedProvider() { return "CUSTOM_HTTP"; }
    }
    
    // ============ 辅助方法 ============
    
    private static SmsInboundMessage createStubInbound(Map<String, Object> rawData, String provider) {
        return SmsInboundMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .from("stub_from")
                .to("stub_to")
                .content("Stub inbound message from " + provider)
                .messageType(SmsInboundMessage.MessageType.TEXT)
                .receivedTime(LocalDateTime.now())
                .rawData(rawData)
                .build();
    }
    
    private static SmsReport createStubReport(String messageId, String provider) {
        return SmsReport.builder()
                .reportId(messageId)
                .messageId(messageId)
                .currentStatus(SmsReport.ReportStatus.UNKNOWN)
                .statusCode("STUB")
                .statusDescription("Stub report from " + provider)
                .lastUpdatedTime(LocalDateTime.now())
                .build();
    }
    
    private static SmsOutboundMessage createStubOutbound(SmsOutboundMessage original, String provider) {
        original.setMessageId(UUID.randomUUID().toString());
        original.setSendStatus(SmsOutboundMessage.SendStatus.SUBMITTED);
        original.setSentTime(LocalDateTime.now());
        return original;
    }
} 