package com.whaleal.ark.cloud.third.sms.outbound.adapter;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import com.whaleal.ark.cloud.third.sms.outbound.entity.SmsOutboundMessage;
import com.whaleal.ark.cloud.third.sms.outbound.sender.MockOutboundSender;
import com.whaleal.ark.cloud.third.sms.outbound.sender.OutboundSender;
import com.whaleal.ark.cloud.third.sms.spi.SmsExtensionLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下行短信适配器 - 主动发送短信
 * 负责向各个提供商主动发送短信消息
 *
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class OutboundAdapter {

    private final Map<SmsProviderType, OutboundSender> senderMap;

    public OutboundAdapter() {
        this.senderMap = new HashMap<>();
        initializeSenders();
    }

    /**
     * 初始化各提供商的发送器（内置 MOCK + SPI 加载）
     */
    private void initializeSenders() {
        senderMap.put(SmsProviderType.MOCK, new MockOutboundSender());
        senderMap.putAll(SmsExtensionLoader.loadProviders(OutboundSender.class, OutboundSender::getSupportedProvider));
        log.info("下行发送器初始化完成，支持 {} 个提供商", senderMap.size());
    }

    /**
     * 发送单条短信
     *
     * @param providerType 提供商类型
     * @param message 短信消息
     * @param config 提供商配置
     * @return 发送后的短信消息（包含发送结果）
     */
    public SmsOutboundMessage sendMessage(SmsProviderType providerType, SmsOutboundMessage message, SmsProviderConfig config) {
        try {
            log.debug("开始发送短信，提供商: {}, 接收方: {}", providerType, message.getTo());

            OutboundSender sender = senderMap.get(providerType);
            if (sender == null) {
                log.warn("未找到提供商 {} 的发送器", providerType);
                return createUnsupportedMessage(providerType, message);
            }

            SmsOutboundMessage result = sender.sendMessage(message, config);

            // 设置提供商类型
            result.setProviderType(providerType);

            log.debug("短信发送完成，消息ID: {}, 状态: {}", result.getMessageId(), result.getSendStatus());

            return result;

        } catch (Exception e) {
            log.error("发送短信失败，提供商: {}, 接收方: {}, 错误: {}", providerType, message.getTo(), e.getMessage(), e);

            // 返回错误状态的消息
            return createErrorMessage(providerType, message, e.getMessage());
        }
    }

    /**
     * 批量发送短信
     *
     * @param providerType 提供商类型
     * @param messages 短信消息列表
     * @param config 提供商配置
     * @return 发送后的短信消息列表（包含发送结果）
     */
    public List<SmsOutboundMessage> sendMessages(SmsProviderType providerType, List<SmsOutboundMessage> messages, SmsProviderConfig config) {
        try {
            log.debug("开始批量发送短信，提供商: {}, 消息数量: {}", providerType, messages.size());

            OutboundSender sender = senderMap.get(providerType);
            if (sender == null) {
                log.warn("未找到提供商 {} 的发送器", providerType);
                return messages.stream()
                        .map(msg -> createUnsupportedMessage(providerType, msg))
                        .toList();
            }

            List<SmsOutboundMessage> results = sender.sendMessages(messages, config);

            // 设置提供商类型
            results.forEach(result -> result.setProviderType(providerType));

            log.debug("批量短信发送完成，发送数量: {}, 返回数量: {}", messages.size(), results.size());

            return results;

        } catch (Exception e) {
            log.error("批量发送短信失败，提供商: {}, 错误: {}", providerType, e.getMessage(), e);

            // 返回错误状态的消息列表
            return messages.stream()
                    .map(msg -> createErrorMessage(providerType, msg, e.getMessage()))
                    .toList();
        }
    }

    /**
     * 发送模板短信
     *
     * @param providerType 提供商类型
     * @param message 模板短信消息
     * @param config 提供商配置
     * @return 发送后的短信消息（包含发送结果）
     */
    public SmsOutboundMessage sendTemplateMessage(SmsProviderType providerType, SmsOutboundMessage message, SmsProviderConfig config) {
        try {
            log.debug("开始发送模板短信，提供商: {}, 模板ID: {}", providerType,
                    message.getBusinessInfo() != null ? message.getBusinessInfo().getTemplateId() : "未知");

            OutboundSender sender = senderMap.get(providerType);
            if (sender == null) {
                log.warn("未找到提供商 {} 的发送器", providerType);
                return createUnsupportedMessage(providerType, message);
            }

            SmsOutboundMessage result = sender.sendTemplateMessage(message, config);

            // 设置提供商类型
            result.setProviderType(providerType);

            log.debug("模板短信发送完成，消息ID: {}, 状态: {}", result.getMessageId(), result.getSendStatus());

            return result;

        } catch (Exception e) {
            log.error("发送模板短信失败，提供商: {}, 错误: {}", providerType, e.getMessage(), e);

            // 返回错误状态的消息
            return createErrorMessage(providerType, message, e.getMessage());
        }
    }

    /**
     * 创建不支持的消息
     *
     * @param providerType 提供商类型
     * @param originalMessage 原始消息
     * @return 不支持的消息
     */
    private SmsOutboundMessage createUnsupportedMessage(SmsProviderType providerType, SmsOutboundMessage originalMessage) {
        SmsOutboundMessage message = SmsOutboundMessage.builder()
                .messageId(originalMessage.getMessageId())
                .providerType(providerType)
                .from(originalMessage.getFrom())
                .to(originalMessage.getTo())
                .content(originalMessage.getContent())
                .sendStatus(SmsOutboundMessage.SendStatus.FAILED)
                .build();

        // 复制扩展信息
        if (message.getExtraInfo() == null) {
            message.setExtraInfo(new HashMap<>());
        }
        message.getExtraInfo().put("error", "该提供商不支持短信发送");

        return message;
    }

    /**
     * 创建错误消息
     *
     * @param providerType 提供商类型
     * @param originalMessage 原始消息
     * @param errorMessage 错误信息
     * @return 错误消息
     */
    private SmsOutboundMessage createErrorMessage(SmsProviderType providerType, SmsOutboundMessage originalMessage, String errorMessage) {
        SmsOutboundMessage message = SmsOutboundMessage.builder()
                .messageId(originalMessage.getMessageId())
                .providerType(providerType)
                .from(originalMessage.getFrom())
                .to(originalMessage.getTo())
                .content(originalMessage.getContent())
                .sendStatus(SmsOutboundMessage.SendStatus.FAILED)
                .build();

        // 复制扩展信息
        if (message.getExtraInfo() == null) {
            message.setExtraInfo(new HashMap<>());
        }
        message.getExtraInfo().put("error", "短信发送失败: " + errorMessage);

        return message;
    }

    /**
     * 检查是否支持指定提供商
     *
     * @param providerType 提供商类型
     * @return 是否支持
     */
    public boolean isSupported(SmsProviderType providerType) {
        return senderMap.containsKey(providerType);
    }

    /**
     * 获取支持的提供商列表
     *
     * @return 支持的提供商类型数组
     */
    public SmsProviderType[] getSupportedProviders() {
        return senderMap.keySet().toArray(new SmsProviderType[0]);
    }
}
