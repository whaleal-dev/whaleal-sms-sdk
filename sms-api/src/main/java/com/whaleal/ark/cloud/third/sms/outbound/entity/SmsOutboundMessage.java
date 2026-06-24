package com.whaleal.ark.cloud.third.sms.outbound.entity;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 下行短信 - 系统发送给用户的短信
 * Outbound表示从系统平台发送到用户手机的短信消息
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsOutboundMessage {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 提供商消息ID
     */
    private String providerMessageId;
    
    /**
     * 批次ID
     */
    private String batchId;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 提供商类型
     */
    private SmsProviderType providerType;
    
    /**
     * 发送方号码（系统号码/短代码）
     */
    private String from;
    
    /**
     * 接收方手机号（用户号码）
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
     * 发送状态
     */
    private SendStatus sendStatus;
    
    /**
     * 编码类型
     */
    private String encoding;
    
    /**
     * 消息条数
     */
    private Integer messageCount;
    
    /**
     * 拆分信息
     */
    private SplitInfo splitInfo;
    
    /**
     * 发送配置
     */
    private SendConfig sendConfig;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 发送时间
     */
    private LocalDateTime sentTime;
    
    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDeliveryTime;
    
    /**
     * 业务信息
     */
    private BusinessInfo businessInfo;
    
    /**
     * 费用信息
     */
    private CostInfo costInfo;
    
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
        NOTIFICATION("notification", "通知消息"),
        VERIFICATION("verification", "验证码消息"),
        MARKETING("marketing", "营销消息"),
        ALERT("alert", "告警消息"),
        REMINDER("reminder", "提醒消息"),
        REPLY("reply", "回复消息"),
        BROADCAST("broadcast", "广播消息"),
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
     * 发送状态枚举
     */
    public enum SendStatus {
        PENDING("pending", "待发送"),
        SUBMITTED("submitted", "已提交"),
        SENT("sent", "已发送"),
        DELIVERED("delivered", "已送达"),
        FAILED("failed", "发送失败"),
        EXPIRED("expired", "已过期"),
        CANCELLED("cancelled", "已取消"),
        UNKNOWN("unknown", "未知状态");
        
        private final String code;
        private final String description;
        
        SendStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    /**
     * 拆分信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitInfo {
        /**
         * 是否拆分
         */
        private Boolean isSplit;
        
        /**
         * 拆分条数
         */
        private Integer splitCount;
        
        /**
         * 拆分详情
         */
        private List<SplitPart> splitParts;
        
        /**
         * 单条最大长度
         */
        private Integer maxSingleLength;
        
        /**
         * 拆分规则
         */
        private String splitRule;
    }
    
    /**
     * 拆分片段
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitPart {
        /**
         * 片段序号
         */
        private Integer partIndex;
        
        /**
         * 片段内容
         */
        private String partContent;
        
        /**
         * 片段长度
         */
        private Integer partLength;
        
        /**
         * 片段消息ID
         */
        private String partMessageId;
    }
    
    /**
     * 发送配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendConfig {
        /**
         * 发送优先级
         */
        private Integer priority;
        
        /**
         * 定时发送时间
         */
        private LocalDateTime scheduledTime;
        
        /**
         * 有效期（分钟）
         */
        private Integer validityPeriod;
        
        /**
         * 重试次数
         */
        private Integer retryCount;
        
        /**
         * 回调URL
         */
        private String callbackUrl;
        
        /**
         * 是否需要回执
         */
        private Boolean needReceipt;
        
        /**
         * 发送渠道
         */
        private String sendChannel;
    }
    
    /**
     * 业务信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInfo {
        /**
         * 业务类型
         */
        private String businessType;
        
        /**
         * 业务场景
         */
        private String businessScene;
        
        /**
         * 模板ID
         */
        private String templateId;
        
        /**
         * 模板参数
         */
        private Map<String, String> templateParams;
        
        /**
         * 签名
         */
        private String signature;
        
        /**
         * 业务标签
         */
        private List<String> businessTags;
        
        /**
         * 关联业务ID
         */
        private String relatedBusinessId;
    }
    
    /**
     * 费用信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostInfo {
        /**
         * 费用金额
         */
        private String amount;
        
        /**
         * 货币单位
         */
        private String currency;
        
        /**
         * 计费类型
         */
        private String billingType;
        
        /**
         * 消息条数
         */
        private Integer messageCount;
        
        /**
         * 单价
         */
        private String unitPrice;
        
        /**
         * 计费时间
         */
        private LocalDateTime billingTime;
    }
} 