package com.whaleal.ark.cloud.third.sms.receipt.entity;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 短信回执 - 最终送达确认
 * Receipt表示短信最终是否成功送达到用户手机的确认信息
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsReceipt {
    
    /**
     * 回执ID
     */
    private String receiptId;
    
    /**
     * 原始消息ID
     */
    private String messageId;
    
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
     * 接收方手机号
     */
    private String to;
    
    /**
     * 回执状态
     */
    private ReceiptStatus receiptStatus;
    
    /**
     * 原始状态代码
     */
    private String receiptCode;
    
    /**
     * 回执描述
     */
    private String receiptDescription;
    
    /**
     * 错误代码（如果送达失败）
     */
    private String errorCode;
    
    /**
     * 错误描述（如果送达失败）
     */
    private String errorDescription;
    
    /**
     * 送达时间（用户实际收到短信的时间）
     */
    private LocalDateTime deliveredTime;
    
    /**
     * 回执接收时间（平台收到回执的时间）
     */
    private LocalDateTime receivedTime;
    
    /**
     * 费用信息
     */
    private CostInfo costInfo;
    
    /**
     * 网络信息
     */
    private NetworkInfo networkInfo;
    
    /**
     * 原始回执数据
     */
    private Map<String, Object> rawData;
    
    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;
    
    /**
     * 回执状态枚举
     */
    public enum ReceiptStatus {
        DELIVERED("delivered", "已送达"),
        SENT("sent", "已发送"),
        FAILED("failed", "送达失败"),
        EXPIRED("expired", "已过期"),
        REJECTED("rejected", "被拒绝"),
        UNDELIVERABLE("undeliverable", "无法送达"),
        UNKNOWN("unknown", "未知状态");
        
        private final String code;
        private final String description;
        
        ReceiptStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
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
    }
    
    /**
     * 网络信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkInfo {
        /**
         * 运营商名称
         */
        private String carrierName;
        
        /**
         * 运营商代码
         */
        private String carrierCode;
        
        /**
         * 国家代码
         */
        private String countryCode;
        
        /**
         * 网络类型
         */
        private String networkType;
    }
} 