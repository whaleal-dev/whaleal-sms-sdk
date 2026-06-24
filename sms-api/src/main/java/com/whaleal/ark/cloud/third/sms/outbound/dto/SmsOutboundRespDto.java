package com.whaleal.ark.cloud.third.sms.outbound.dto;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SMS Outbound Response DTO
 */
@Data
@Builder
public class SmsOutboundRespDto {
    
    private String messageId;
    
    private String requestId;
    
    private String code;
    
    private String message;
    
    private String providerMessageId;
    
    private String providerType;
    
    private Integer messageCount;
    
    private BigDecimal price;
    
    private String currency;
    
    private LocalDateTime sendTime;
    
    private Integer status;
    
    private String errorCode;
    
    private String errorMessage;
    
    /**
     * 可选字段，用于存储特定于云平台的额外信息
     */
    private Object extraInfo;
} 