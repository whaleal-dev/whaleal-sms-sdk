package com.whaleal.ark.cloud.third.sms.inbound.entity;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 上行短信 - 用户发送给系统的短信
 * Inbound表示从用户手机发送到系统平台的短信消息
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsInboundMessage {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 提供商消息ID
     */
    private String providerMessageId;
    
    /**
     * 提供商类型
     */
    private SmsProviderType providerType;
    
    /**
     * 发送方手机号（用户号码）
     */
    private String from;
    
    /**
     * 接收方号码（系统号码/短代码）
     */
    private String to;
    
    /**
     * 短信内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private MessageType messageType;
    
    /**
     * 编码类型
     */
    private String encoding;
    
    /**
     * 消息条数
     */
    private Integer messageCount;
    
    /**
     * 关键词信息
     */
    private KeywordInfo keywordInfo;
    
    /**
     * 指令信息
     */
    private CommandInfo commandInfo;
    
    /**
     * 用户发送时间
     */
    private LocalDateTime sentTime;
    
    /**
     * 系统接收时间
     */
    private LocalDateTime receivedTime;
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    /**
     * 上下文信息
     */
    private ContextInfo contextInfo;
    
    /**
     * 原始消息数据
     */
    private Map<String, Object> rawData;
    
    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TEXT("text", "文本消息"),
        KEYWORD("keyword", "关键词消息"),
        COMMAND("command", "指令消息"),
        REPLY("reply", "回复消息"),
        SUBSCRIPTION("subscription", "订阅消息"),
        UNSUBSCRIPTION("unsubscription", "退订消息"),
        UNSUBSCRIBE("unsubscribe", "退订消息"),
        VERIFICATION("verification", "验证码消息"),
        UNKNOWN("unknown", "未知类型");
        
        private final String code;
        private final String description;
        
        MessageType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    /**
     * 关键词信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordInfo {
        /**
         * 识别的关键词
         */
        private String keyword;
        
        /**
         * 关键词类型
         */
        private String keywordType;
        
        /**
         * 匹配规则
         */
        private String matchRule;
        
        /**
         * 置信度
         */
        private Double confidence;
        
        /**
         * 可能的关键词列表
         */
        private List<String> possibleKeywords;
    }
    
    /**
     * 指令信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommandInfo {
        /**
         * 指令名称
         */
        private String command;
        
        /**
         * 指令参数
         */
        private Map<String, String> parameters;
        
        /**
         * 指令类型
         */
        private String commandType;
        
        /**
         * 是否有效指令
         */
        private Boolean isValid;
        
        /**
         * 错误信息（如果指令无效）
         */
        private String errorMessage;
    }
    
    /**
     * 用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户ID（如果已识别）
         */
        private String userId;
        
        /**
         * 用户标签
         */
        private List<String> userTags;
        
        /**
         * 用户地区
         */
        private String userRegion;
        
        /**
         * 运营商信息
         */
        private String carrier;
        
        /**
         * 是否新用户
         */
        private Boolean isNewUser;
        
        /**
         * 最后活跃时间
         */
        private LocalDateTime lastActiveTime;
    }
    
    /**
     * 上下文信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextInfo {
        /**
         * 会话ID
         */
        private String sessionId;
        
        /**
         * 对话轮次
         */
        private Integer conversationTurn;
        
        /**
         * 上一条消息ID
         */
        private String previousMessageId;
        
        /**
         * 相关下行消息ID
         */
        private String relatedOutboundMessageId;
        
        /**
         * 业务场景
         */
        private String businessScene;
        
        /**
         * 处理状态
         */
        private String processStatus;
    }
} 