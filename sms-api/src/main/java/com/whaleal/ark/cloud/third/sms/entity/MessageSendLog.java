package com.whaleal.ark.cloud.third.sms.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 短信发送日志
 */
@Data
@Accessors(chain = true)
public class MessageSendLog {
    private String busiNo;
    private String messageType;
    private String telPhone;
    private String content;
    private String sendStatus;
    private Date sendTime;
    private String errorCode;
    private String errorMessage;
}
