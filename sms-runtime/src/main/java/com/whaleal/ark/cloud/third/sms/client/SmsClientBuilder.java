package com.whaleal.ark.cloud.third.sms.client;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.core.SmsModuleManager;
import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * 纯 Java 场景下的 {@link SmsClient} 构建器（无需 Spring）
 * <p>仅设置默认供应商与非敏感项；秘钥与供应商可在每次 {@link SmsSendRequest} 中动态传入。</p>
 */
public final class SmsClientBuilder {

    private SmsProviderConfig baseConfig = SmsProviderConfig.builder()
            .providerType(SmsProviderType.MOCK)
            .name(SmsProviderType.MOCK.getDisplayName())
            .build();

    public SmsClientBuilder provider(SmsProviderType provider) {
        this.baseConfig = baseConfig.toBuilder()
                .providerType(provider)
                .name(provider.getDisplayName())
                .build();
        return this;
    }

    public SmsClientBuilder region(String region) {
        this.baseConfig = baseConfig.toBuilder().region(region).build();
        return this;
    }

    public SmsClientBuilder signName(String signName) {
        this.baseConfig = baseConfig.toBuilder().signName(signName).build();
        return this;
    }

    public SmsClientBuilder defaultFrom(String defaultFrom) {
        this.baseConfig = baseConfig.toBuilder().defaultFrom(defaultFrom).build();
        return this;
    }

    public SmsClientBuilder baseUrl(String baseUrl) {
        this.baseConfig = baseConfig.toBuilder().baseUrl(baseUrl).build();
        return this;
    }

    /** 全局默认送达回执 URL（可被单次 {@link SmsSendRequest#getCallbackUrl()} 覆盖） */
    public SmsClientBuilder deliveryReceiptUrl(String deliveryReceiptUrl) {
        this.baseConfig = baseConfig.toBuilder().deliveryReceiptUrl(deliveryReceiptUrl).build();
        return this;
    }

    /** 全局默认回调 URL（deliveryReceiptUrl 未设置时的备选） */
    public SmsClientBuilder callbackUrl(String callbackUrl) {
        this.baseConfig = baseConfig.toBuilder().callbackUrl(callbackUrl).build();
        return this;
    }

    public SmsClient build() {
        return new DefaultSmsClient(baseConfig, new SmsModuleManager());
    }
}
