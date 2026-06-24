package com.whaleal.ark.cloud.third.sms.client;

/**
 * {@link SmsClient} 工厂入口（运行时模块）
 */
public final class SmsClients {

    private SmsClients() {
    }

    public static SmsClientBuilder builder() {
        return new SmsClientBuilder();
    }
}
