package com.whaleal.ark.cloud.third.sms.inbound.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SMS Inbound Log Entity
 */
@Data
public class SmsInboundLog {

    private String id;

    /**
     * Message ID
     */
    private String messageId;

    /**
     * Provider message ID
     */
    private String providerMessageId;

    /**
     * Provider type
     */
    private String providerType;

    /**
     * From number
     */
    private String from;

    /**
     * To number
     */
    private String to;

    /**
     * Message content
     */
    private String content;

    /**
     * Message type
     */
    private String messageType;

    /**
     * Encoding
     */
    private String encoding;

    /**
     * Message count
     */
    private Integer messageCount;

    /**
     * Sent time
     */
    private LocalDateTime sentTime;

    /**
     * Received time
     */
    private LocalDateTime receivedTime;

    /**
     * Processing status
     * 0: Unprocessed
     * 1: Success
     * 2: Failed, waiting for retry
     * 3: Failed
     * 4: Exception, config error
     * 5: MMS content, not processed
     * 9: Customer not accepting webhook
     */
    private Integer status;

    /**
     * Error code
     */
    private String errorCode;

    /**
     * Error message
     */
    private String errorMessage;

    /**
     * Response message
     */
    private String response;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String username;

    /**
     * Config ID
     */
    private String configId;

    /**
     * Phone number
     */
    private String phone;

    /**
     * Webhook URL
     */
    private String url;
} 