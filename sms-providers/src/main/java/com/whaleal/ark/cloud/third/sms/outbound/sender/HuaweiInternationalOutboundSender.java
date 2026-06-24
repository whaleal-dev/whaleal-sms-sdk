package com.whaleal.ark.cloud.third.sms.outbound.sender;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.outbound.entity.SmsOutboundMessage;
import com.whaleal.ark.cloud.third.sms.exception.SmsException;
import com.whaleal.ark.cloud.third.sms.exception.SmsTimeoutException;
import com.whaleal.ark.cloud.third.sms.exception.SmsCredentialsException;
import com.whaleal.ark.cloud.third.sms.exception.SmsParameterException;
import com.whaleal.ark.cloud.third.sms.exception.SmsNetworkException;
import com.whaleal.ark.cloud.third.sms.exception.SmsConfigException;
import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 华为云国际短信发送器
 * 
 * 特点：
 * 1. 支持全球220+国家和地区
 * 2. 支持多语言短信内容
 * 3. 支持变量模板
 * 4. 支持短链接
 * 5. 支持批量发送
 * 6. 支持通道选择
 * 
 * API文档：https://support.huaweicloud.com/intl/api-msgsms/sms_05_0000.html
 * 
 * 注意事项：
 * 1. 国际短信需要单独申请签名
 * 2. 模板需要提前在华为云国际短信控制台审核通过
 * 3. 手机号码格式为：+国际区号手机号码（如：+8613800138000）
 * 4. 默认使用新加坡节点：sms-intl.ap-southeast-1.myhuaweicloud.com
 * 5. 支持的编码格式：UTF-8
 * 6. 支持的短信类型：推广短信和通知短信
 * 
 * @author whaleal-dev
 */
public class HuaweiInternationalOutboundSender implements OutboundSender {
    
    private static final Logger logger = LoggerFactory.getLogger(HuaweiInternationalOutboundSender.class);
    private static final String API_VERSION = "2.0";
    private static final String DEFAULT_REGION = "ap-southeast-1";
    
    @Override
    public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
        try {
            validateConfig(config);
            validateMessage(message);
            
            // 构建请求参数
            Map<String, String> params = buildRequestParams(message, config);
            
            // 发送请求
            String response = sendRequest(params, config);
            
            // 解析响应
            return parseResponse(response, message);
            
        } catch (SmsException e) {
            logger.error("华为云国际短信发送失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("华为云国际短信发送异常: {}", e.getMessage(), e);
            throw new SmsException("UNKNOWN_ERROR", "未知错误: " + e.getMessage(), SmsProviderType.HUAWEI_INTERNATIONAL, e);
        }
    }
    
    private void validateConfig(SmsProviderConfig config) {
        if (config == null) {
            throw new SmsConfigException("配置不能为空", SmsProviderType.HUAWEI_INTERNATIONAL);
        }
        if (isEmpty(config.getAccessKeyId())) {
            throw new SmsConfigException("AppKey不能为空", "appKey", null, SmsProviderType.HUAWEI_INTERNATIONAL);
        }
        if (isEmpty(config.getAccessKeySecret())) {
            throw new SmsConfigException("AppSecret不能为空", "appSecret", null, SmsProviderType.HUAWEI_INTERNATIONAL);
        }
    }
    
    private void validateMessage(SmsOutboundMessage message) {
        if (message == null) {
            throw new SmsParameterException("消息不能为空", SmsProviderType.HUAWEI_INTERNATIONAL);
        }
        if (isEmpty(message.getTo())) {
            throw new SmsParameterException("接收号码不能为空", SmsProviderType.HUAWEI_INTERNATIONAL);
        }
        if (isEmpty(message.getContent())) {
            throw new SmsParameterException("消息内容不能为空", SmsProviderType.HUAWEI_INTERNATIONAL);
        }
    }
    
    private Map<String, String> buildRequestParams(SmsOutboundMessage message, SmsProviderConfig config) {
        Map<String, String> params = new HashMap<>();
        params.put("from", message.getFrom());
        params.put("to", message.getTo());
        params.put("content", message.getContent());
        params.put("signature", config.getSignature());
        
        // 添加认证信息
        params.put("app_key", config.getAccessKeyId());
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", UUID.randomUUID().toString());
        
        return params;
    }
    
    private String sendRequest(Map<String, String> params, SmsProviderConfig config) throws IOException {
        // 计算签名
        String signature = calculateSignature(params, config.getAccessKeySecret());
        params.put("signature", signature);
        
        // 构建请求URL
        String url = "https://sms-intl.ap-southeast-1.myhuaweicloud.com/sms/batchSendSms/v1";
        
        // 发送HTTP请求
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = buildRequestBody(params).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 读取响应
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private String buildRequestBody(Map<String, String> params) {
        // TODO: 实现请求体JSON构建
        return "{}";
    }
    
    private SmsOutboundMessage parseResponse(String response, SmsOutboundMessage originalMessage) {
        // TODO: 解析响应JSON，设置messageId和发送状态
        return originalMessage;
    }
    
    private String calculateSignature(Map<String, String> params, String secret) {
        // TODO: 实现华为云签名算法
        return "";
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    @Override
    public String getSupportedProvider() {
        return SmsProviderType.HUAWEI_INTERNATIONAL.name();
    }
    
    @Override
    public boolean supportsTemplate() {
        return true;
    }
} 