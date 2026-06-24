package com.whaleal.ark.cloud.third.sms.outbound.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * SMS Outbound Request DTO
 */
@Data
public class SmsOutboundReqDto {

    @NotBlank(message = "Recipient number cannot be empty")
    private String to;

    private String from;

    @NotBlank(message = "Message content cannot be empty")
    private String content;

    @NotNull(message = "Application ID cannot be empty")

    /**
     * 应用key
     */
    @NotNull(message = "Application ID cannot be empty")
    private String appKey;

    /**
     * 应用密钥
     */
    @NotNull(message = "Application Secret cannot be empty")
    private String appSecret;


    /**
     * 用户ID - 从发信请求的早期阶段就传递
     */
    private Long userId;

    /**
     * 用户名 - 用于日志记录
     */
    private String username;

    /**
     * 应用名称 - 用于日志记录
     */
    private String applicationName;

    private String type = "text";

    private String templateId;

    private LocalDateTime scheduledTime;

    private String callbackUrl;

    private String referenceId;

    private String extraParams;
}
