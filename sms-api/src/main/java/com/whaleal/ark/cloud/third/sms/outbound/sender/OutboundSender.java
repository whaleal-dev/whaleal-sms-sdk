package com.whaleal.ark.cloud.third.sms.outbound.sender;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.outbound.entity.SmsOutboundMessage;
import com.whaleal.ark.cloud.third.sms.exception.SmsException;

import java.util.List;

/**
 * 下行短信发送器接口
 * 定义各个提供商下行短信发送的统一规范
 * 
 * <h3>参数顺序规范</h3>
 * 所有方法统一使用以下参数顺序：
 * <ol>
 *   <li>业务数据（message/messages）</li>
 *   <li>配置参数（config）</li>
 * </ol>
 * 
 * <h3>返回值规范</h3>
 * <ul>
 *   <li>单条发送：返回包含发送结果的 SmsOutboundMessage</li>
 *   <li>批量发送：返回包含发送结果的 List&lt;SmsOutboundMessage&gt;</li>
 *   <li>发送失败时，返回包含错误信息的对象，不抛出异常</li>
 * </ul>
 * 
 * <h3>异常处理规范</h3>
 * <ul>
 *   <li>配置错误、网络错误等系统级异常：抛出 SmsException 及其子类</li>
 *   <li>业务级错误（如号码格式错误）：返回包含错误状态的结果对象</li>
 * </ul>
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
public interface OutboundSender {
    
    /**
     * 发送单条短信
     * 
     * <h4>参数规范</h4>
     * <ul>
     *   <li>message: 必填，包含完整的短信内容和接收方信息</li>
     *   <li>config: 必填，包含提供商认证和配置信息</li>
     * </ul>
     * 
     * <h4>返回值规范</h4>
     * <ul>
     *   <li>成功：返回包含 messageId、sendStatus=SUBMITTED 的消息对象</li>
     *   <li>失败：返回包含 sendStatus=FAILED、错误信息的消息对象</li>
     * </ul>
     * 
     * <h4>异常规范</h4>
     * <ul>
     *   <li>SmsConfigException: 配置参数错误</li>
     *   <li>SmsTimeoutException: 请求超时</li>
     *   <li>SmsNetworkException: 网络连接错误</li>
     *   <li>SmsException: 其他系统级错误</li>
     * </ul>
     * 
     * @param message 短信消息对象，包含发送方、接收方、内容等信息
     * @param config 提供商配置，包含认证信息、超时设置等
     * @return 发送后的短信消息，包含发送结果、消息ID、状态等信息
     * @throws SmsException 当发生系统级错误时抛出
     */
    SmsOutboundMessage sendMessage(SmsOutboundMessage message, SmsProviderConfig config) throws SmsException;
    
    /**
     * 批量发送短信
     * 
     * <h4>参数规范</h4>
     * <ul>
     *   <li>messages: 必填，短信消息列表，每个消息包含完整信息</li>
     *   <li>config: 必填，包含提供商认证和配置信息</li>
     * </ul>
     * 
     * <h4>返回值规范</h4>
     * <ul>
     *   <li>返回与输入数量相同的结果列表</li>
     *   <li>每个结果包含对应消息的发送状态</li>
     *   <li>部分成功时，成功的消息标记为 SUBMITTED，失败的标记为 FAILED</li>
     * </ul>
     * 
     * <h4>默认实现</h4>
     * 如果提供商不支持批量发送，默认实现会逐个调用单条发送方法。
     * 
     * @param messages 短信消息列表
     * @param config 提供商配置
     * @return 发送后的短信消息列表，包含每个消息的发送结果
     * @throws SmsException 当发生系统级错误时抛出
     */
    default List<SmsOutboundMessage> sendMessages(List<SmsOutboundMessage> messages, SmsProviderConfig config) throws SmsException {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("消息列表不能为空");
        }
        
        return messages.stream()
                .map(message -> {
                    try {
                        return sendMessage(message, config);
                    } catch (SmsException e) {
                        // 将异常转换为失败状态的消息对象
                        return createFailedMessage(message, e.getMessage());
                    }
                })
                .toList();
    }
    
    /**
     * 发送模板短信
     * 
     * <h4>参数规范</h4>
     * <ul>
     *   <li>message: 必填，包含模板ID、模板参数等模板短信信息</li>
     *   <li>config: 必填，包含提供商认证和配置信息</li>
     * </ul>
     * 
     * <h4>模板信息获取</h4>
     * 模板相关信息通过 message.getBusinessInfo() 获取：
     * <ul>
     *   <li>templateId: 模板ID</li>
     *   <li>templateParams: 模板参数</li>
     * </ul>
     * 
     * <h4>默认实现</h4>
     * 如果提供商不区分普通短信和模板短信，默认实现会调用普通发送方法。
     * 
     * @param message 模板短信消息对象，包含模板ID和参数
     * @param config 提供商配置
     * @return 发送后的短信消息，包含发送结果
     * @throws SmsException 当发生系统级错误时抛出
     */
    default SmsOutboundMessage sendTemplateMessage(SmsOutboundMessage message, SmsProviderConfig config) throws SmsException {
        // 默认实现：使用普通发送方式
        return sendMessage(message, config);
    }
    
    /**
     * 验证消息是否有效
     * 
     * <h4>验证规则</h4>
     * <ul>
     *   <li>消息对象不能为空</li>
     *   <li>接收方号码不能为空</li>
     *   <li>消息内容不能为空（模板短信除外）</li>
     *   <li>模板短信必须包含模板ID</li>
     * </ul>
     * 
     * @param message 待验证的短信消息
     * @return true: 消息有效，false: 消息无效
     */
    default boolean isValidMessage(SmsOutboundMessage message) {
        if (message == null) {
            return false;
        }
        
        // 验证接收方号码
        if (message.getTo() == null || message.getTo().trim().isEmpty()) {
            return false;
        }
        
        // 验证消息内容或模板信息
        boolean hasContent = message.getContent() != null && !message.getContent().trim().isEmpty();
        boolean hasTemplate = message.getBusinessInfo() != null 
                && message.getBusinessInfo().getTemplateId() != null 
                && !message.getBusinessInfo().getTemplateId().trim().isEmpty();
        
        return hasContent || hasTemplate;
    }
    
    /**
     * 验证配置是否有效
     * 
     * <h4>验证规则</h4>
     * <ul>
     *   <li>配置对象不能为空</li>
     *   <li>必要的认证信息不能为空</li>
     *   <li>URL、端点等配置信息格式正确</li>
     * </ul>
     * 
     * @param config 待验证的提供商配置
     * @return true: 配置有效，false: 配置无效
     */
    default boolean isValidConfig(SmsProviderConfig config) {
        if (config == null) {
            return false;
        }
        
        // 基础验证：至少需要一个认证信息
        return (config.getApiKey() != null && !config.getApiKey().trim().isEmpty())
                || (config.getAccessKeyId() != null && !config.getAccessKeyId().trim().isEmpty())
                || (config.getApiSecret() != null && !config.getApiSecret().trim().isEmpty());
    }
    
    /**
     * 创建失败状态的消息对象
     * 
     * @param originalMessage 原始消息
     * @param errorMessage 错误信息
     * @return 失败状态的消息对象
     */
    default SmsOutboundMessage createFailedMessage(SmsOutboundMessage originalMessage, String errorMessage) {
        return SmsOutboundMessage.builder()
                .messageId(originalMessage.getMessageId())
                .from(originalMessage.getFrom())
                .to(originalMessage.getTo())
                .content(originalMessage.getContent())
                .sendStatus(SmsOutboundMessage.SendStatus.FAILED)
                .businessInfo(originalMessage.getBusinessInfo())
                .extraInfo(originalMessage.getExtraInfo())
                .build();
    }
    
    /**
     * 获取发送器支持的提供商类型
     * 
     * <h4>返回值规范</h4>
     * 返回提供商的标准名称，应与 SmsProviderType 枚举值对应。
     * 
     * @return 提供商类型名称，如 "TWILIO"、"ALIYUN" 等
     */
    String getSupportedProvider();
    
    /**
     * 获取发送器的超时配置
     * 
     * <h4>默认值</h4>
     * 如果未特别配置，默认返回 10000 毫秒（10秒）。
     * 
     * @return 超时时间（毫秒）
     */
    default long getTimeoutMs() {
        return 10000L; // 默认10秒超时
    }
    
    /**
     * 是否支持批量发送
     * 
     * <h4>默认实现</h4>
     * 默认返回 false，表示使用逐个发送的方式实现批量。
     * 如果提供商支持真正的批量API，应重写此方法返回 true。
     * 
     * @return true: 支持批量发送，false: 不支持批量发送
     */
    default boolean supportsBatchSending() {
        return false;
    }
    
    /**
     * 是否支持模板短信
     * 
     * <h4>默认实现</h4>
     * 默认返回 false，表示将模板短信作为普通短信发送。
     * 如果提供商有专门的模板短信API，应重写此方法返回 true。
     * 
     * @return true: 支持模板短信，false: 不支持模板短信
     */
    default boolean supportsTemplate() {
        return false;
    }
} 