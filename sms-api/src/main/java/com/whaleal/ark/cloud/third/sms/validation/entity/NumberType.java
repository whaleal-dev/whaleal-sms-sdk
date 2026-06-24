package com.whaleal.ark.cloud.third.sms.validation.entity;

import lombok.Getter;

/**
 * 号码类型枚举
 */
@Getter
public enum NumberType {
    /**
     * 移动电话
     */
    MOBILE("mobile", "移动电话"),

    /**
     * 固定电话
     */
    FIXED_LINE("fixed_line", "固定电话"),

    /**
     * 免费电话
     */
    TOLL_FREE("toll_free", "免费电话"),

    /**
     * 付费电话
     */
    PREMIUM_RATE("premium_rate", "付费电话"),

    /**
     * 共享费用
     */
    SHARED_COST("shared_cost", "共享费用"),

    /**
     * 网络电话
     */
    VOIP("voip", "网络电话"),

    /**
     * 个人号码
     */
    PERSONAL_NUMBER("personal_number", "个人号码"),

    /**
     * 寻呼机
     */
    PAGER("pager", "寻呼机"),

    /**
     * 通用接入号码
     */
    UAN("uan", "通用接入号码"),

    /**
     * 短代码
     */
    SHORT_CODE("short_code", "短代码"),

    /**
     * 未知类型
     */
    UNKNOWN("unknown", "未知类型");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    NumberType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举值
     *
     * @param code 代码
     * @return 枚举值
     */
    public static NumberType fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return UNKNOWN;
        }
        for (NumberType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return String.format("%s[code=%s, description=%s]", 
            name(), code, description);
    }
} 