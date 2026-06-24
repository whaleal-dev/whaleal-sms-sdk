package com.whaleal.ark.cloud.third.sms.report;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.report.entity.SmsReport;

/**
 * 短信状态报告获取器接口
 */
public interface ReportFetcher {
    
    /**
     * 获取短信状态报告
     *
     * @param messageId 消息ID
     * @param config 短信服务商配置
     * @return 短信状态报告
     */
    SmsReport fetchReport(String messageId, SmsProviderConfig config);

    /**
     * 获取支持的服务商类型
     *
     * @return 服务商类型名称
     */
    String getSupportedProvider();
} 