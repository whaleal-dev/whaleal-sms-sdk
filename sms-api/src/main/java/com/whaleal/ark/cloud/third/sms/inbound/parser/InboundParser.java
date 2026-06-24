package com.whaleal.ark.cloud.third.sms.inbound.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInboundMessage;

import java.util.Map;

/**
 * 上行短信解析器接口
 * 定义各个提供商上行短信数据解析的统一规范
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
public interface InboundParser {
    
    /**
     * 解析上行短信数据
     * 
     * @param rawData 原始上行短信数据
     * @param config 提供商配置
     * @return 解析后的上行短信对象
     */
    SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config);
    
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