package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 参数异常
 * 当请求参数无效、缺失或格式错误时抛出
 * 
 */
public class SmsParameterException extends SmsException {
    
    /**
     * 参数名称
     */
    private final String parameterName;
    
    /**
     * 参数值
     */
    private final Object parameterValue;
    
    public SmsParameterException(String message) {
        super("INVALID_PARAMETER", message);
        this.parameterName = null;
        this.parameterValue = null;
    }
    
    public SmsParameterException(String message, SmsProviderType providerType) {
        super("INVALID_PARAMETER", message, providerType);
        this.parameterName = null;
        this.parameterValue = null;
    }
    
    public SmsParameterException(String message, String parameterName, Object parameterValue) {
        super("INVALID_PARAMETER", message);
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }
    
    public SmsParameterException(String message, String parameterName, Object parameterValue, SmsProviderType providerType) {
        super("INVALID_PARAMETER", message, providerType);
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public Object getParameterValue() {
        return parameterValue;
    }
    
    /**
     * 必填参数缺失
     */
    public static SmsParameterException missingRequired(String parameterName) {
        return new SmsParameterException(
                String.format("必填参数缺失: %s", parameterName), 
                parameterName, 
                null
        );
    }
    
    /**
     * 手机号码格式无效
     */
    public static SmsParameterException invalidPhoneNumber(String phoneNumber) {
        return new SmsParameterException(
                String.format("手机号码格式无效: %s", phoneNumber), 
                "phoneNumber", 
                phoneNumber
        );
    }
    
    /**
     * 消息内容无效
     */
    public static SmsParameterException invalidMessage(String message, String reason) {
        return new SmsParameterException(
                String.format("消息内容无效: %s (%s)", message, reason), 
                "message", 
                message
        );
    }
    
    /**
     * 参数值超出范围
     */
    public static SmsParameterException outOfRange(String parameterName, Object value, Object min, Object max) {
        return new SmsParameterException(
                String.format("参数 %s 的值 %s 超出范围 [%s, %s]", parameterName, value, min, max), 
                parameterName, 
                value
        );
    }
    
    /**
     * 参数格式错误
     */
    public static SmsParameterException invalidFormat(String parameterName, Object value, String expectedFormat) {
        return new SmsParameterException(
                String.format("参数 %s 的值 %s 格式错误，期望格式: %s", parameterName, value, expectedFormat), 
                parameterName, 
                value
        );
    }
    
    /**
     * 批量参数数量超限
     */
    public static SmsParameterException batchSizeExceeded(int actualSize, int maxSize) {
        return new SmsParameterException(
                String.format("批量操作数量超限: %d (最大支持: %d)", actualSize, maxSize), 
                "batchSize", 
                actualSize
        );
    }
    
    /**
     * 模板参数错误
     */
    public static SmsParameterException invalidTemplateParam(String templateId, String paramName, Object paramValue) {
        return new SmsParameterException(
                String.format("模板 %s 的参数 %s 值无效: %s", templateId, paramName, paramValue), 
                "templateParam", 
                paramValue
        );
    }
    
    /**
     * 号码格式错误 - 国际号码格式
     */
    public static SmsParameterException invalidInternationalFormat(String phoneNumber) {
        return new SmsParameterException(
                String.format("手机号码格式错误，必须包含国家代码: %s", phoneNumber), 
                "phoneNumber", 
                phoneNumber
        );
    }
    
    /**
     * 号码长度错误
     */
    public static SmsParameterException invalidPhoneLength(String phoneNumber, int minLength, int maxLength) {
        return new SmsParameterException(
                String.format("手机号码长度错误: %s (长度: %d, 要求: %d-%d)", phoneNumber, phoneNumber.length(), minLength, maxLength), 
                "phoneNumber", 
                phoneNumber
        );
    }
    
    /**
     * 不支持的国家代码
     */
    public static SmsParameterException unsupportedCountryCode(String phoneNumber, String countryCode) {
        return new SmsParameterException(
                String.format("不支持的国家代码: %s (号码: %s)", countryCode, phoneNumber), 
                "countryCode", 
                countryCode
        );
    }
    
    /**
     * 号码在黑名单中
     */
    public static SmsParameterException phoneNumberBlacklisted(String phoneNumber) {
        return new SmsParameterException(
                String.format("手机号码在黑名单中: %s", phoneNumber), 
                "phoneNumber", 
                phoneNumber
        );
    }
    
    /**
     * 无效的虚拟号码
     */
    public static SmsParameterException virtualPhoneNumber(String phoneNumber) {
        return new SmsParameterException(
                String.format("不支持虚拟号码: %s", phoneNumber), 
                "phoneNumber", 
                phoneNumber
        );
    }
    
    /**
     * 号码已停机
     */
    public static SmsParameterException phoneNumberInactive(String phoneNumber) {
        return new SmsParameterException(
                String.format("手机号码已停机或不存在: %s", phoneNumber), 
                "phoneNumber", 
                phoneNumber
        );
    }
    
    /**
     * JSON格式错误
     */
    public static SmsParameterException invalidJsonFormat(String parameterName, String jsonContent, String error) {
        return new SmsParameterException(
                String.format("JSON格式错误 (%s): %s", parameterName, error), 
                parameterName, 
                jsonContent
        );
    }
    
    /**
     * 日期格式错误
     */
    public static SmsParameterException invalidDateFormat(String parameterName, String dateValue, String expectedFormat) {
        return new SmsParameterException(
                String.format("日期格式错误: %s，期望格式: %s", dateValue, expectedFormat), 
                parameterName, 
                dateValue
        );
    }
    
    /**
     * 编码格式错误
     */
    public static SmsParameterException invalidEncoding(String parameterName, String content, String encoding) {
        return new SmsParameterException(
                String.format("编码格式错误: %s 不是有效的 %s 编码", content, encoding), 
                parameterName, 
                content
        );
    }
    
    @Override
    public String getFullErrorMessage() {
        String baseMessage = super.getFullErrorMessage();
        if (parameterName != null) {
            baseMessage += String.format(" (参数: %s", parameterName);
            if (parameterValue != null) {
                baseMessage += String.format(", 值: %s", parameterValue);
            }
            baseMessage += ")";
        }
        return baseMessage;
    }
} 