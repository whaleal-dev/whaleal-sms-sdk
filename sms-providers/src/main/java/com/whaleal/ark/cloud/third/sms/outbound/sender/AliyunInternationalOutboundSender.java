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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 阿里云国际短信发送器
 * 
 * 特点：
 * 1. 支持全球200+国家和地区
 * 2. 支持多语言短信内容
 * 3. 支持变量模板
 * 4. 支持短链接
 * 
 * API文档：https://help.aliyun.com/document_detail/419273.html
 * 
 * 注意事项：
 * 1. 国际短信不支持携带签名
 * 2. 模板内容需要提前在阿里云国际短信控制台审核通过
 * 3. 手机号码格式必须是带国际区号的格式（如：+8613800138000）
 * 4. 默认使用新加坡节点：dysmsapi-intl.aliyuncs.com
 * 
 * @author whaleal-dev
 */
public class AliyunInternationalOutboundSender implements OutboundSender {
    
    private static final Logger logger = LoggerFactory.getLogger(AliyunInternationalOutboundSender.class);
    private static final String API_VERSION = "2018-05-01";
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
            logger.error("阿里云国际短信发送失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("阿里云国际短信发送异常: {}", e.getMessage(), e);
            throw new SmsException("UNKNOWN_ERROR", "未知错误: " + e.getMessage(), SmsProviderType.ALIYUN_INTERNATIONAL, e);
        }
    }
    
    private void validateConfig(SmsProviderConfig config) {
        if (config == null) {
            throw new SmsConfigException("配置不能为空", SmsProviderType.ALIYUN_INTERNATIONAL);
        }
        if (isEmpty(config.getAccessKeyId())) {
            throw new SmsConfigException("AccessKeyId不能为空", "accessKeyId", null, SmsProviderType.ALIYUN_INTERNATIONAL);
        }
        if (isEmpty(config.getAccessKeySecret())) {
            throw new SmsConfigException("AccessKeySecret不能为空", "accessKeySecret", null, SmsProviderType.ALIYUN_INTERNATIONAL);
        }
    }
    
    private void validateMessage(SmsOutboundMessage message) {
        if (message == null) {
            throw new SmsParameterException("消息不能为空", SmsProviderType.ALIYUN_INTERNATIONAL);
        }
        if (isEmpty(message.getTo())) {
            throw new SmsParameterException("接收号码不能为空", SmsProviderType.ALIYUN_INTERNATIONAL);
        }
        if (isEmpty(message.getContent())) {
            throw new SmsParameterException("消息内容不能为空", SmsProviderType.ALIYUN_INTERNATIONAL);
        }
    }
    
    private Map<String, String> buildRequestParams(SmsOutboundMessage message, SmsProviderConfig config) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "SendMessageToGlobe");
        params.put("Version", API_VERSION);
        params.put("RegionId", DEFAULT_REGION);
        params.put("To", message.getTo());
        params.put("Message", message.getContent());
        
        // 如果有发送方ID，则设置
        if (!isEmpty(message.getFrom())) {
            params.put("From", message.getFrom());
        }
        
        // 添加签名和时间戳
        params.put("Timestamp", getIso8601Time());
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        
        return params;
    }
    
    private String sendRequest(Map<String, String> params, SmsProviderConfig config) throws IOException {
        // 计算签名
        String signature = calculateSignature(params, config.getAccessKeySecret());
        params.put("Signature", signature);
        
        // 构建请求URL
        String url = buildRequestUrl(params);
        
        // 发送HTTP请求
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
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
    
    private SmsOutboundMessage parseResponse(String response, SmsOutboundMessage originalMessage) {
        // TODO: 解析响应JSON，设置messageId和发送状态
        return originalMessage;
    }
    
    private String calculateSignature(Map<String, String> params, String secret) {
        // TODO: 实现阿里云签名算法
        return "";
    }
    
    private String buildRequestUrl(Map<String, String> params) {
        StringBuilder url = new StringBuilder("https://dysmsapi-intl.aliyuncs.com/?");
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                   .append("=")
                   .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                   .append("&");
            }
        } catch (UnsupportedEncodingException e) {
            throw new SmsException("ENCODING_ERROR", "URL编码错误", SmsProviderType.ALIYUN_INTERNATIONAL, e);
        }
        return url.substring(0, url.length() - 1);
    }
    
    private String getIso8601Time() {
        // TODO: 实现ISO8601时间格式化
        return new Date().toString();
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    @Override
    public String getSupportedProvider() {
        return SmsProviderType.ALIYUN_INTERNATIONAL.name();
    }
    
    @Override
    public boolean supportsTemplate() {
        return true;
    }
} 