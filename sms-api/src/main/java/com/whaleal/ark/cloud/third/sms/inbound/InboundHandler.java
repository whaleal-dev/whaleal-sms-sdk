package com.whaleal.ark.cloud.third.sms.inbound;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInbound;

/**
 * 短信入站处理器接口
 */
public interface InboundHandler {

    /**
     * 处理入站短信
     *
     * @param rawData 原始数据
     * @param config 服务商配置
     * @return 入站短信实体
     */
    SmsInbound handleInbound(String rawData, SmsProviderConfig config);

    /**
     * 获取支持的服务商类型
     *
     * @return 服务商类型名称
     */
    String getSupportedProvider();
} 