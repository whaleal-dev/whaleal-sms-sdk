package com.whaleal.ark.cloud.third.sms.enums;

/**
 * SMS服务提供商类型枚举
 *
 */
public enum SmsProviderType {

    /**
     * Vonage (原Nexmo)
     */
    VONAGE("vonage", "Vonage", "全球优先"),

    /**
     * 阿里云短信服务（中国区域）
     */
    ALIYUN("aliyun", "阿里云短信", "中国优先"),

    /**
     * 阿里云国际短信服务
     */
    ALIYUN_INTERNATIONAL("aliyun_international", "阿里云国际短信", "亚太优先"),

    /**
     * 腾讯云短信服务（中国区域）
     */
    TENCENT("tencent", "腾讯云短信", "中国优先"),

    /**
     * 腾讯云国际短信服务
     */
    TENCENT_INTERNATIONAL("tencent_international", "腾讯云国际短信", "亚太优先"),

    /**
     * 华为云短信服务（中国区域）
     */
    HUAWEI("huawei", "华为云短信", "中国优先"),

    /**
     * 华为云国际短信服务
     */
    HUAWEI_INTERNATIONAL("huawei_international", "华为云国际短信", "亚太优先"),

    /**
     * Twilio
     */
    TWILIO("twilio", "Twilio", "全球优先"),

    /**
     * Amazon SNS
     */
    AWS("aws", "Amazon", "全球优先"),

    /**
     * 中国移动短信平台
     */
    CHINA_MOBILE("china_mobile", "中国移动", "中国优先"),

    /**
     * 中国电信短信平台
     */
    CHINA_TELECOM("china_telecom", "中国电信", "中国优先"),

    /**
     * 中国联通短信平台
     */
    CHINA_UNICOM("china_unicom", "中国联通", "中国优先"),

    /**
     * 自定义HTTP接口
     */
    CUSTOM_HTTP("custom_http", "自定义HTTP", "可配置"),

    /**
     * 测试模拟平台
     */
    MOCK("mock", "测试模拟", "测试环境"),

    /**
     * MessageBird
     */
    MESSAGEBIRD("messagebird", "MessageBird", "欧洲优先"),

    /**
     * Plivo
     */
    PLIVO("plivo", "Plivo", "美洲优先"),

    /**
     * Infobip
     */
    INFOBIP("infobip", "Infobip", "全球优先");

    private final String code;
    private final String displayName;
    private final String region;

    SmsProviderType(String code, String displayName, String region) {
        this.code = code;
        this.displayName = displayName;
        this.region = region;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrimaryRegion() {
        return region;
    }

    /**
     * 根据代码获取枚举值
     * @param code 代码
     * @return 枚举值
     */
    public static SmsProviderType fromCode(String code) {
        for (SmsProviderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的SMS提供商类型: " + code);
    }

    /**
     * 是否为云服务提供商
     * @return true表示是云服务提供商
     */
    public boolean isCloudProvider() {
        return this == ALIYUN || this == ALIYUN_INTERNATIONAL || 
               this == TENCENT || this == TENCENT_INTERNATIONAL || 
               this == HUAWEI || this == HUAWEI_INTERNATIONAL || 
               this == AWS;
    }

    /**
     * 是否为国际短信平台
     * @return true表示是国际短信平台
     */
    public boolean isInternationalPlatform() {
        return this == ALIYUN_INTERNATIONAL || this == TENCENT_INTERNATIONAL || 
               this == HUAWEI_INTERNATIONAL;
    }

    /**
     * 是否支持国际短信
     * @return true表示支持国际短信
     */
    public boolean isInternationalProvider() {
        return isInternationalPlatform() ||
               this == VONAGE || this == TWILIO || this == AWS || 
               this == MESSAGEBIRD || this == PLIVO || this == INFOBIP ||
               this == CUSTOM_HTTP; // 自定义HTTP接口可配置为国际短信
    }

    /**
     * 是否支持国内短信
     * @return true表示支持国内短信
     */
    public boolean isDomesticProvider() {
        return this == ALIYUN || this == TENCENT || this == HUAWEI ||
               this == CHINA_MOBILE || this == CHINA_TELECOM || this == CHINA_UNICOM ||
               this == CUSTOM_HTTP; // 自定义HTTP接口可配置为国内短信
    }
}
