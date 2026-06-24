package com.whaleal.ark.cloud.third.sms.receipt.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.receipt.entity.SmsReceipt;

import java.util.Map;

/**
 * 回执解析器接口
 * 定义各个提供商回执数据解析的统一规范
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
public interface ReceiptParser {
    
    /**
     * 解析回执数据
     * 
     * @param rawData 原始回执数据
     * @param config 提供商配置
     * @return 解析后的回执对象
     */
    SmsReceipt parse(Map<String, Object> rawData, SmsProviderConfig config);
    
    /**
     * 验证原始数据是否有效
     * 
     * @param rawData 原始数据
     * @return 是否有效
     */
    default boolean isValidData(Map<String, Object> rawData) {
        return rawData != null && !rawData.isEmpty();
    }
    
    /**
     * 获取解析器支持的提供商类型
     * 
     * @return 提供商类型名称
     */
    String getSupportedProvider();
} 