package com.whaleal.ark.cloud.third.sms.validation.validator;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.validation.entity.PhoneValidationResult;

import java.util.List;

/**
 * 号码校验器接口
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
public interface PhoneValidator {
    
    /**
     * 校验单个号码
     * 
     * @param phoneNumber 待校验的号码
     * @param config 提供商配置
     * @return 校验结果
     */
    PhoneValidationResult validate(String phoneNumber, SmsProviderConfig config);
    
    /**
     * 批量校验号码
     * 
     * @param phoneNumbers 待校验的号码列表
     * @param config 提供商配置
     * @return 校验结果列表
     */
    default List<PhoneValidationResult> validateBatch(List<String> phoneNumbers, SmsProviderConfig config) {
        return phoneNumbers.stream()
                .map(phone -> validate(phone, config))
                .toList();
    }
    
    /**
     * 快速校验（仅检查格式）
     * 
     * @param phoneNumber 待校验的号码
     * @return 是否为有效格式
     */
    default boolean isValidFormat(String phoneNumber) {
        try {
            PhoneValidationResult result = validate(phoneNumber, null);
            return result.getIsValid() != null && result.getIsValid();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取支持的提供商标识
     * 
     * @return 提供商标识
     */
    String getSupportedProvider();
    
    /**
     * 是否支持批量校验
     * 
     * @return true表示支持批量校验
     */
    default boolean supportsBatchValidation() {
        return false;
    }
    
    /**
     * 获取校验器的费用信息
     * 
     * @return 费用描述
     */
    default String getCostInfo() {
        return "免费";
    }
    
    /**
     * 获取校验器的限制信息
     * 
     * @return 限制描述
     */
    default String getLimitInfo() {
        return "无限制";
    }
} 