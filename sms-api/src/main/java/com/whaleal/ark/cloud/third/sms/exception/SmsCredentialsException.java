package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 密钥认证异常
 * 当API密钥无效、过期或权限不足时抛出
 * 
 */
public class SmsCredentialsException extends SmsException {
    
    public SmsCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message);
    }
    
    public SmsCredentialsException(String message, SmsProviderType providerType) {
        super("INVALID_CREDENTIALS", message, providerType);
    }
    
    public SmsCredentialsException(String message, SmsProviderType providerType, Throwable cause) {
        super("INVALID_CREDENTIALS", message, providerType, cause);
    }
    
    /**
     * API密钥无效
     */
    public static SmsCredentialsException invalidApiKey(String apiKey, SmsProviderType providerType) {
        return new SmsCredentialsException(
                String.format("API密钥无效或已过期: %s", maskApiKey(apiKey)), 
                providerType
        );
    }
    
    /**
     * API密码无效
     */
    public static SmsCredentialsException invalidApiSecret(SmsProviderType providerType) {
        return new SmsCredentialsException("API密码无效或已过期", providerType);
    }
    
    /**
     * 访问密钥无效
     */
    public static SmsCredentialsException invalidAccessKey(String accessKeyId, SmsProviderType providerType) {
        return new SmsCredentialsException(
                String.format("访问密钥无效或已过期: %s", maskApiKey(accessKeyId)), 
                providerType
        );
    }
    
    /**
     * 权限不足
     */
    public static SmsCredentialsException insufficientPermissions(SmsProviderType providerType) {
        return new SmsCredentialsException("权限不足，请检查API密钥的权限配置", providerType);
    }
    
    /**
     * 账户被禁用
     */
    public static SmsCredentialsException accountDisabled(SmsProviderType providerType) {
        return new SmsCredentialsException("账户已被禁用或暂停", providerType);
    }
    
    /**
     * 掩码API密钥，只显示前后几位
     */
    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
    }
} 