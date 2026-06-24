package com.whaleal.ark.cloud.third.sms.client;

/**
 * 短信发送模式
 */
public enum SmsSendMode {

    /** 1 个号码 + 1 条内容 */
    ONE_TO_ONE,

    /** 多个号码 + 同一条内容（或同一模板） */
    ONE_TO_MANY,

    /** 多条独立消息（号码、内容、凭证可各不相同） */
    MANY_TO_MANY
}
