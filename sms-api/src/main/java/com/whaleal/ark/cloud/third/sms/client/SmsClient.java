package com.whaleal.ark.cloud.third.sms.client;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 短信 SDK 统一门面（对外主入口）
 * <p>
 * 不读取配置文件；Spring 项目可注入默认 Bean 或自行 {@code @Bean} 注册，
 * 纯 Java 项目通过 runtime 模块的 {@code SmsClients.builder()} 构建实例。
 */
public interface SmsClient {

    SmsSendResult send(SmsSendRequest request);

    List<SmsSendResult> sendBatch(List<SmsSendRequest> requests);

    CompletableFuture<SmsSendResult> sendAsync(SmsSendRequest request);

    /** 使用指定配置发送（覆盖全局默认配置） */
    SmsSendResult send(SmsSendRequest request, SmsProviderConfig config);

    SmsProviderType getDefaultProvider();
}
