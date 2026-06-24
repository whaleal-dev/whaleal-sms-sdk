package com.whaleal.ark.cloud.third.sms.inbound.entity;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 入站短信实体
 */
@Data
@Builder
public class SmsInbound {

    /**
     * 入站消息ID
     */
    private String inboundId;

    /**
     * 发送方号码
     */
    private String fromNumber;

    /**
     * 接收方号码
     */
    private String toNumber;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 服务商类型
     */
    private SmsProviderType providerType;

    /**
     * 接收时间
     */
    private LocalDateTime receivedTime;

    /**
     * 原始数据
     */
    private Map<String, Object> rawData;
} 