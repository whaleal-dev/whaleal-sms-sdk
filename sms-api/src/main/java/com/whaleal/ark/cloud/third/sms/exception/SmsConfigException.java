package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 配置异常
 * 当SDK配置错误、缺失或无效时抛出
 * 
 */
public class SmsConfigException extends SmsException {
    
    /**
     * 配置项名称
     */
    private final String configKey;
    
    /**
     * 配置项值
     */
    private final Object configValue;
    
    public SmsConfigException(String message) {
        super("INVALID_CONFIG", message);
        this.configKey = null;
        this.configValue = null;
    }
    
    public SmsConfigException(String message, String configKey, Object configValue) {
        super("INVALID_CONFIG", message);
        this.configKey = configKey;
        this.configValue = configValue;
    }
    
    public SmsConfigException(String message, SmsProviderType providerType) {
        super("INVALID_CONFIG", message, providerType);
        this.configKey = null;
        this.configValue = null;
    }
    
    public SmsConfigException(String message, String configKey, Object configValue, SmsProviderType providerType) {
        super("INVALID_CONFIG", message, providerType);
        this.configKey = configKey;
        this.configValue = configValue;
    }
    
    public String getConfigKey() {
        return configKey;
    }
    
    public Object getConfigValue() {
        return configValue;
    }
    
    /**
     * 配置项缺失
     */
    public static SmsConfigException missingConfig(String configKey, SmsProviderType providerType) {
        return new SmsConfigException(
                String.format("配置项缺失: %s", configKey), 
                configKey, 
                null, 
                providerType
        );
    }
    
    /**
     * 配置项值无效
     */
    public static SmsConfigException invalidConfigValue(String configKey, Object configValue, String reason, SmsProviderType providerType) {
        return new SmsConfigException(
                String.format("配置项 %s 的值 %s 无效: %s", configKey, configValue, reason), 
                configKey, 
                configValue, 
                providerType
        );
    }
    
    /**
     * 不支持的提供商类型
     */
    public static SmsConfigException unsupportedProvider(SmsProviderType providerType) {
        return new SmsConfigException(
                String.format("不支持的SMS提供商: %s", providerType.getDisplayName()), 
                providerType
        );
    }
    
    /**
     * 提供商配置冲突
     */
    public static SmsConfigException conflictingConfigs(String config1, String config2, SmsProviderType providerType) {
        return new SmsConfigException(
                String.format("配置冲突: %s 和 %s 不能同时设置", config1, config2), 
                providerType
        );
    }
    
    /**
     * 超时配置无效
     */
    public static SmsConfigException invalidTimeout(int timeoutMs, SmsProviderType providerType) {
        return new SmsConfigException(
                String.format("超时配置无效: %d ms (必须大于0且小于300000)", timeoutMs), 
                "timeout", 
                timeoutMs, 
                providerType
        );
    }
    
    /**
     * URL配置无效
     */
    public static SmsConfigException invalidUrl(String url, SmsProviderType providerType) {
        return new SmsConfigException(
                String.format("URL配置无效: %s", url), 
                "url", 
                url, 
                providerType
        );
    }
    
    /**
     * 加密配置错误
     */
    public static SmsConfigException encryptionConfigError(String algorithm, SmsProviderType providerType, Throwable cause) {
        return new SmsConfigException(
                String.format("加密配置错误: %s", algorithm), 
                "encryption", 
                algorithm, 
                providerType
        );
    }
    
    @Override
    public String getFullErrorMessage() {
        String baseMessage = super.getFullErrorMessage();
        if (configKey != null) {
            baseMessage += String.format(" (配置项: %s", configKey);
            if (configValue != null) {
                baseMessage += String.format(", 值: %s", configValue);
            }
            baseMessage += ")";
        }
        return baseMessage;
    }
} 