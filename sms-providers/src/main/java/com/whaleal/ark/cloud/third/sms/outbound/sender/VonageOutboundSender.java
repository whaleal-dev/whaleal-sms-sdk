package com.whaleal.ark.cloud.third.sms.outbound.sender;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.outbound.entity.SmsOutboundMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * Vonage下行短信发送器
 * 实现真实的Vonage API调用
 *
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class VonageOutboundSender implements OutboundSender {

    private final HttpClient httpClient;
    private static final String VONAGE_SMS_ENDPOINT = "https://rest.nexmo.com/sms/json";

    public VonageOutboundSender() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) {
        try {
            log.info("Vonage发送短信 - 接收方: {}, 内容长度: {}", message.getTo(),
                    message.getContent() != null ? message.getContent().length() : 0);

            // 构建请求参数
            String requestBody = buildRequestBody(message, config);

            // 发送HTTP请求
            HttpResponse<String> response = sendHttpRequest(requestBody, config);

            // 解析响应
            return parseResponse(message, response);

        } catch (Exception e) {
            log.error("Vonage发送短信失败，接收方: {}, 错误: {}", message.getTo(), e.getMessage(), e);
            return createFailedMessage(message, e.getMessage());
        }
    }

    /**
     * 构建请求体
     */
    private String buildRequestBody(SmsOutboundMessage message, SmsProviderConfig config) {
        Map<String, String> params = new HashMap<>();
        params.put("api_key", config.getApiKey());
        params.put("api_secret", config.getApiSecret());
        params.put("from", message.getFrom() != null ? message.getFrom() : config.getDefaultFrom());
        params.put("to", message.getTo());
        params.put("text", message.getContent());

        // 添加编码类型参数
        // Vonage支持: text(GSM-7默认), unicode(UCS-2), binary
        String encoding = message.getEncoding();
        if (encoding != null) {
            if ("unicode".equalsIgnoreCase(encoding) || "ucs2".equalsIgnoreCase(encoding)) {
                params.put("type", "unicode");
            } else if ("binary".equalsIgnoreCase(encoding)) {
                params.put("type", "binary");
            } else {
                params.put("type", "text"); // GSM-7
            }
        } else {
            // 默认使用text，让Vonage自动检测
            params.put("type", "text");
        }

        // 添加可选参数
        if (message.getSendConfig() != null) {
            if (message.getSendConfig().getCallbackUrl() != null) {
                params.put("callback", message.getSendConfig().getCallbackUrl());
            }
            if (message.getSendConfig().getValidityPeriod() != null) {
                params.put("ttl", String.valueOf(message.getSendConfig().getValidityPeriod() * 60 * 1000)); // 转换为毫秒
            }
        }

        // 构建URL编码的请求体
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(java.net.URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return sb.toString();
    }

    /**
     * 发送HTTP请求
     */
    private HttpResponse<String> sendHttpRequest(String requestBody, SmsProviderConfig config) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VONAGE_SMS_ENDPOINT))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }




    /**
     * 将JSON字符串解析为SmsOutboundMessage实体
     * 支持多条消息的解析（自动拆分场景）
     *
     * @return 解析后的SmsOutboundMessage对象
     */
    public SmsOutboundMessage parseResponse(SmsOutboundMessage originalMessage, HttpResponse<String> response) {
        try {
            String responseBody = response.body();
            log.debug("Vonage响应数据: {}", responseBody);
            
            // 解析顶层JSON对象
            JSONObject root = JSON.parseObject(responseBody);

            // 获取消息列表
            JSONArray messages = root.getJSONArray("messages");
            if (messages == null || messages.isEmpty()) {
                log.warn("Vonage响应中没有消息数据");
                return createFailedMessage(originalMessage, "No messages in response");
            }

            int messageCount = root.getInteger("message-count");
            log.info("Vonage响应包含 {} 条消息", messageCount);

            // 处理多条消息（自动拆分场景）
            if (messageCount > 1) {
                return parseMultipleMessages(originalMessage, messages, root);
            } else {
                // 处理单条消息
                return parseSingleMessage(originalMessage, messages.getJSONObject(0), root);
            }
                    
        } catch (Exception e) {
            log.error("解析Vonage响应失败", e);
            return createFailedMessage(originalMessage, "Failed to parse Vonage response: " + e.getMessage());
        }
    }

    /**
     * 解析多条消息（自动拆分场景）
     */
    private SmsOutboundMessage parseMultipleMessages(SmsOutboundMessage originalMessage, JSONArray messages, JSONObject root) {
        log.info("解析多条消息，总数: {}", messages.size());
        
        List<SmsOutboundMessage.SplitPart> splitParts = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;
        boolean hasError = false;
        String errorMessage = null;
        
        // 解析每条消息
        for (int i = 0; i < messages.size(); i++) {
            JSONObject messageJson = messages.getJSONObject(i);
            
            // 检查是否有错误
            String errorCode = messageJson.getString("error-code");
            String errorText = messageJson.getString("error-text");
            
            if (errorCode != null && !errorCode.equals("0")) {
                hasError = true;
                errorMessage = String.format("Message %d error: %s - %s", i + 1, errorCode, errorText);
                log.error("Vonage消息 {} 发送失败 - 错误码: {}, 错误信息: {}", i + 1, errorCode, errorText);
            }
            
            // 构建拆分片段
            SmsOutboundMessage.SplitPart splitPart = SmsOutboundMessage.SplitPart.builder()
                    .partIndex(i + 1)
                    .partContent(originalMessage.getContent()) // 使用原始内容
                    .partLength(originalMessage.getContent() != null ? originalMessage.getContent().length() : 0)
                    .partMessageId(messageJson.getString("message-id"))
                    .build();
            
            splitParts.add(splitPart);
            
            // 累计费用
            String messagePrice = messageJson.getString("message-price");
            if (messagePrice != null) {
                try {
                    totalCost = totalCost.add(new BigDecimal(messagePrice));
                } catch (NumberFormatException e) {
                    log.warn("无法解析消息价格: {}", messagePrice);
                }
            }
        }
        
        // 构建拆分信息
        SmsOutboundMessage.SplitInfo splitInfo = SmsOutboundMessage.SplitInfo.builder()
                .isSplit(true)
                .splitCount(messages.size())
                .splitParts(splitParts)
                .maxSingleLength(160) // GSM7编码单条最大长度
                .splitRule("GSM7_160_CHARS")
                .build();
        
        // 收集所有消息的详细信息
        List<String> allMessageIds = new ArrayList<>();
        List<String> allNetworks = new ArrayList<>();
        List<String> allPrices = new ArrayList<>();
        String lastRemainingBalance = null;
        
        for (int i = 0; i < messages.size(); i++) {
            JSONObject messageJson = messages.getJSONObject(i);
            allMessageIds.add(messageJson.getString("message-id"));
            allNetworks.add(messageJson.getString("network"));
            allPrices.add(messageJson.getString("message-price"));
            lastRemainingBalance = messageJson.getString("remaining-balance"); // 使用最后一条的余额
        }
        
        // 构建费用信息
        SmsOutboundMessage.CostInfo costInfo = SmsOutboundMessage.CostInfo.builder()
                .amount(totalCost.toString())
                .messageCount(messages.size())
                .billingTime(LocalDateTime.now())
                .currency("EUR")
                .billingType("per_message")
                .unitPrice(allPrices.get(0)) // 使用第一条的单价作为基准
                .build();
        
        // 构建业务信息
        SmsOutboundMessage.BusinessInfo businessInfo = SmsOutboundMessage.BusinessInfo.builder()
                .businessType("sms_outbound")
                .businessScene("notification")
                .signature(originalMessage.getFrom())
                .build();
        
        // 构建扩展信息
        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("networks", allNetworks); // 所有网络信息
        extraInfo.put("remaining_balance", lastRemainingBalance); // 最终余额
        extraInfo.put("total_cost", totalCost.toString());
        extraInfo.put("split_count", messages.size());
        extraInfo.put("vonage_message_ids", allMessageIds);
        extraInfo.put("unit_prices", allPrices); // 每条消息的单价
        extraInfo.put("primary_message_id", allMessageIds.get(0)); // 主要消息ID（第一条）
        
        if (hasError) {
            extraInfo.put("error_message", errorMessage);
        }
        
        // 构建原始数据映射
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("originalResponse", root);
        rawData.put("allMessages", messages);
        
        // 确定整体状态
        SmsOutboundMessage.SendStatus overallStatus = hasError ? 
                SmsOutboundMessage.SendStatus.FAILED : 
                SmsOutboundMessage.SendStatus.SENT;
        
        // 构建主消息对象
        return SmsOutboundMessage.builder()
                .messageId(originalMessage.getMessageId())
                .providerMessageId(allMessageIds.get(0)) // 使用第一条作为主要ID
                .to(originalMessage.getTo())
                .from(originalMessage.getFrom())
                .content(originalMessage.getContent())
                .messageCount(messages.size())
                .splitInfo(splitInfo)
                .sendStatus(overallStatus)
                .costInfo(costInfo)
                .businessInfo(businessInfo)
                .extraInfo(extraInfo)
                .rawData(rawData)
                .createdTime(LocalDateTime.now())
                .sentTime(LocalDateTime.now())
                .providerType(com.whaleal.ark.cloud.third.sms.enums.SmsProviderType.VONAGE)
                .build();
    }
    
    /**
     * 解析单条消息
     */
    private SmsOutboundMessage parseSingleMessage(SmsOutboundMessage originalMessage, JSONObject messageJson, JSONObject root) {
        // 检查是否有错误
        String errorCode = messageJson.getString("error-code");
        String errorText = messageJson.getString("error-text");
        if (errorCode != null && !errorCode.equals("0")) {
            log.error("Vonage发送失败 - 错误码: {}, 错误信息: {}", errorCode, errorText);
            
            // 构建包含错误信息的失败消息
            Map<String, Object> errorExtraInfo = new HashMap<>();
            errorExtraInfo.put("error_code", errorCode);
            errorExtraInfo.put("error_text", errorText);
            errorExtraInfo.put("vonage_message_id", messageJson.getString("message-id"));
            
            return SmsOutboundMessage.builder()
                    .messageId(originalMessage.getMessageId())
                    .providerMessageId(messageJson.getString("message-id"))
                    .from(originalMessage.getFrom())
                    .to(originalMessage.getTo())
                    .content(originalMessage.getContent())
                    .sendStatus(SmsOutboundMessage.SendStatus.FAILED)
                    .extraInfo(errorExtraInfo)
                    .providerType(com.whaleal.ark.cloud.third.sms.enums.SmsProviderType.VONAGE)
                    .createdTime(LocalDateTime.now())
                    .build();
        }

        // 构建费用信息
        SmsOutboundMessage.CostInfo costInfo = SmsOutboundMessage.CostInfo.builder()
                .amount(messageJson.getString("message-price"))
                .messageCount(root.getInteger("message-count"))
                .billingTime(LocalDateTime.now())
                .currency("EUR") // Vonage使用欧元作为默认货币
                .billingType("per_message")
                .unitPrice(messageJson.getString("message-price"))
                .build();

        // 构建业务信息
        SmsOutboundMessage.BusinessInfo businessInfo = SmsOutboundMessage.BusinessInfo.builder()
                .businessType("sms_outbound")
                .businessScene("notification")
                .signature(originalMessage.getFrom())
                .build();

        // 构建扩展信息
        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("network", messageJson.getString("network"));
        extraInfo.put("remaining_balance", messageJson.getString("remaining-balance"));
        extraInfo.put("message_price", messageJson.getString("message-price"));
        extraInfo.put("vonage_message_id", messageJson.getString("message-id"));

        // 构建原始数据映射（保留所有原始字段）
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("originalResponse", root);
        rawData.put("vonageResponse", messageJson);

        // 构建主消息对象
        return SmsOutboundMessage.builder()
                .messageId(originalMessage.getMessageId())  // ✅ 保持原始messageId
                .providerMessageId(messageJson.getString("message-id"))
                .to(messageJson.getString("to"))
                .from(originalMessage.getFrom())
                .content(originalMessage.getContent())
                .messageCount(root.getInteger("message-count"))
                .sendStatus(mapStatus(messageJson.getString("status")))
                .costInfo(costInfo)
                .businessInfo(businessInfo)
                .extraInfo(extraInfo)
                .rawData(rawData)
                .createdTime(LocalDateTime.now())
                .sentTime(LocalDateTime.now())
                .providerType(com.whaleal.ark.cloud.third.sms.enums.SmsProviderType.VONAGE)
                .build();
    }

    /**
     * 将JSON中的状态码映射为SendStatus枚举
     * 根据Vonage官方文档：https://developer.vonage.com/en/api/sms/overview#response-codes
     * @param statusCode 状态码字符串
     * @return 对应的SendStatus枚举
     */
    private static SmsOutboundMessage.SendStatus mapStatus(String statusCode) {
        if (statusCode == null) {
            return SmsOutboundMessage.SendStatus.UNKNOWN;
        }
        
        // 根据Vonage官方文档映射状态码
        switch (statusCode) {
            case "0":
                return SmsOutboundMessage.SendStatus.SENT; // 成功发送
            case "1":
                return SmsOutboundMessage.SendStatus.PENDING; // 待发送
            case "2":
                return SmsOutboundMessage.SendStatus.FAILED; // 发送失败
            case "3":
                return SmsOutboundMessage.SendStatus.FAILED; // 无效号码
            case "4":
                return SmsOutboundMessage.SendStatus.FAILED; // 无法路由
            case "5":
                return SmsOutboundMessage.SendStatus.FAILED; // 字符集不支持
            case "6":
                return SmsOutboundMessage.SendStatus.FAILED; // 消息太长
            case "7":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "8":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "9":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "10":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "11":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "12":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "13":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "14":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "15":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "16":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "17":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "18":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "19":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "20":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "21":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "22":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "23":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "24":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "25":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "26":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "27":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "28":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "29":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "30":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "31":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "32":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "33":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "34":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "35":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "36":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "37":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "38":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "39":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "40":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "41":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "42":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "43":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "44":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "45":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "46":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "47":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "48":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "49":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            case "50":
                return SmsOutboundMessage.SendStatus.FAILED; // 路由错误
            default:
                return SmsOutboundMessage.SendStatus.UNKNOWN;
        }
    }


    @Override
    public String getSupportedProvider() {
        return "VONAGE";
    }
}
