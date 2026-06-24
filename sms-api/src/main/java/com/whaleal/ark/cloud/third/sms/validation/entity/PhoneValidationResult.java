package com.whaleal.ark.cloud.third.sms.validation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 号码校验结果
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneValidationResult {
    
    /**
     * 原始号码
     */
    private String originalNumber;
    
    /**
     * 标准化号码（E.164格式）
     */
    private String standardizedNumber;
    
    /**
     * 校验状态
     */
    private ValidationStatus status;
    
    /**
     * 号码类型
     */
    private NumberType numberType;
    
    /**
     * 国家代码
     */
    private String countryCode;
    
    /**
     * 国家名称
     */
    private String countryName;
    
    /**
     * 运营商信息
     */
    private CarrierInfo carrierInfo;
    
    /**
     * 地理位置信息
     */
    private LocationInfo locationInfo;
    
    /**
     * 是否为有效号码
     */
    private Boolean isValid;
    
    /**
     * 是否可发送短信
     */
    private Boolean canReceiveSms;
    
    /**
     * 是否为移动号码
     */
    private Boolean isMobile;
    
    /**
     * 风险评估
     */
    private RiskAssessment riskAssessment;
    
    /**
     * 校验时间
     */
    private Date validatedTime;
    
    /**
     * 校验耗时（毫秒）
     */
    private Long validationDuration;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 原始响应数据
     */
    private Map<String, Object> rawData;
    
    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;
    
    /**
     * 校验状态枚举
     */
    public enum ValidationStatus {
        VALID("valid", "有效"),
        INVALID("invalid", "无效"),
        UNKNOWN("unknown", "未知"),
        ERROR("error", "校验错误"),
        RATE_LIMITED("rate_limited", "频率限制"),
        UNSUPPORTED("unsupported", "不支持的号码类型");
        
        private final String code;
        private final String description;
        
        ValidationStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    /**
     * 号码类型枚举
     */
    public enum NumberType {
        MOBILE("mobile", "移动电话"),
        FIXED_LINE("fixed_line", "固定电话"),
        TOLL_FREE("toll_free", "免费电话"),
        PREMIUM_RATE("premium_rate", "付费电话"),
        SHARED_COST("shared_cost", "共享费用"),
        VOIP("voip", "网络电话"),
        PERSONAL_NUMBER("personal_number", "个人号码"),
        PAGER("pager", "寻呼机"),
        UAN("uan", "通用接入号码"),
        VOICEMAIL("voicemail", "语音信箱"),
        UNKNOWN("unknown", "未知类型");
        
        private final String code;
        private final String description;
        
        NumberType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    /**
     * 运营商信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarrierInfo {
        /**
         * 运营商名称
         */
        private String name;
        
        /**
         * 运营商代码
         */
        private String code;
        
        /**
         * 移动国家代码（MCC）
         */
        private String mcc;
        
        /**
         * 移动网络代码（MNC）
         */
        private String mnc;
        
        /**
         * 网络类型
         */
        private String networkType;
    }
    
    /**
     * 地理位置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        /**
         * 城市
         */
        private String city;
        
        /**
         * 省份/州
         */
        private String state;
        
        /**
         * 国家
         */
        private String country;
        
        /**
         * 时区
         */
        private String timezone;
        
        /**
         * 经度
         */
        private Double longitude;
        
        /**
         * 纬度
         */
        private Double latitude;
    }
    
    /**
     * 风险评估
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessment {
        /**
         * 风险等级
         */
        private RiskLevel riskLevel;
        
        /**
         * 风险评分（0-100）
         */
        private Integer riskScore;
        
        /**
         * 是否为一次性号码
         */
        private Boolean isDisposable;
        
        /**
         * 是否为虚拟号码
         */
        private Boolean isVirtual;
        
        /**
         * 风险标签
         */
        private String[] riskTags;
        
        /**
         * 风险描述
         */
        private String riskDescription;
    }
    
    /**
     * 风险等级枚举
     */
    public enum RiskLevel {
        LOW("low", "低风险"),
        MEDIUM("medium", "中风险"),
        HIGH("high", "高风险"),
        UNKNOWN("unknown", "未知风险");
        
        private final String code;
        private final String description;
        
        RiskLevel(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
} 