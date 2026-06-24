package com.whaleal.ark.cloud.third.sms.validation.validator;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.validation.entity.PhoneValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 本地号码校验器 - 免费的基础校验
 * 基于正则表达式和号码规则进行本地校验，无需调用外部API
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class LocalPhoneValidator implements PhoneValidator {
    
    // 国家代码与正则表达式映射
    private static final Map<String, CountryRule> COUNTRY_RULES = new HashMap<>();
    
    static {
        // 初始化各国号码规则
        initializeCountryRules();
    }
    
    @Override
    public PhoneValidationResult validate(String phoneNumber, SmsProviderConfig config) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("开始本地校验号码: {}", phoneNumber);
            
            // 清理号码
            String cleanNumber = cleanPhoneNumber(phoneNumber);
            
            // 检测国家代码
            CountryRule countryRule = detectCountry(cleanNumber);
            
            // 校验号码格式
            boolean isValid = validateFormat(cleanNumber, countryRule);
            
            // 构建结果
            PhoneValidationResult result = buildResult(phoneNumber, cleanNumber, countryRule, isValid, startTime);
            
            log.debug("本地校验完成: {} -> {}", phoneNumber, result.getStatus());
            
            return result;
            
        } catch (Exception e) {
            log.error("本地号码校验失败: {}", phoneNumber, e);
            return createErrorResult(phoneNumber, e.getMessage(), startTime);
        }
    }
    
    /**
     * 清理号码（移除空格、短横线等）
     */
    private String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return "";
        
        return phoneNumber.replaceAll("[\\s\\-\\(\\)\\+]", "");
    }
    
    /**
     * 检测国家代码 - 简化版本，不强制要求国家代码前缀
     */
    private CountryRule detectCountry(String cleanNumber) {
        // 优先尝试国家代码前缀匹配（从长到短）
        for (int i = 4; i >= 1; i--) {
            if (cleanNumber.length() > i) {
                String prefix = cleanNumber.substring(0, i);
                CountryRule rule = COUNTRY_RULES.get(prefix);
                if (rule != null) {
                    String nationalNumber = cleanNumber.substring(i);
                    if (isValidNationalNumberLength(nationalNumber, rule)) {
                        return rule;
                    }
                }
            }
        }
        
        // 如果没有找到国家代码前缀，使用基本格式匹配
        // 使用通用规则：4-18位数字且不以0开头
        if (cleanNumber.matches("^[1-9]\\d{3,17}$")) {
            // 返回一个通用的国家规则，默认为中国（最常用）
            return createGenericCountryRule();
        }
        
        return null;
    }
    
    /**
     * 根据号码格式推断国家
     */
    private CountryRule detectCountryByFormat(String cleanNumber) {
        int length = cleanNumber.length();
        
        // 美国号码：10位数字
        if (length == 10 && cleanNumber.matches("^[2-9]\\d{2}[2-9]\\d{6}$")) {
            return COUNTRY_RULES.get("1");
        }
        
        // 中国号码：11位手机号或7-8位固话
        if (length == 11 && cleanNumber.matches("^1[3-9]\\d{9}$")) {
            return COUNTRY_RULES.get("86");
        }
        if (length >= 7 && length <= 8 && cleanNumber.matches("^[2-9]\\d{6,7}$")) {
            return COUNTRY_RULES.get("86");
        }
        
        // 英国号码：10-11位
        if (length >= 10 && length <= 11 && cleanNumber.matches("^[1-9]\\d{9,10}$")) {
            return COUNTRY_RULES.get("44");
        }
        
        // 默认根据长度推断
        if (length == 10) {
            return COUNTRY_RULES.get("1");  // 美国
        } else if (length == 11) {
            return COUNTRY_RULES.get("86"); // 中国
        }
        
        // 其他情况默认中国
        return COUNTRY_RULES.get("86");
    }
    
    /**
     * 验证去掉国家代码后的号码长度是否合理
     */
    private boolean isValidNationalNumberLength(String nationalNumber, CountryRule rule) {
        if (nationalNumber == null || nationalNumber.isEmpty()) {
            return false;
        }
        
        int length = nationalNumber.length();
        String countryCode = rule.countryCode;
        
        switch (countryCode) {
            case "1":   // 美国
                return length == 10;
            case "86":  // 中国
                return length >= 7 && length <= 11;
            case "44":  // 英国  
                return length >= 9 && length <= 10;
            default:
                return length >= 7 && length <= 15;
        }
    }
    
    /**
     * 校验号码格式
     */
    private boolean validateFormat(String cleanNumber, CountryRule countryRule) {
        if (countryRule == null || countryRule.pattern == null) {
            return false;
        }
        
        return countryRule.pattern.matcher(cleanNumber).matches();
    }
    
    /**
     * 构建校验结果
     */
    private PhoneValidationResult buildResult(String originalNumber, String cleanNumber, 
                                            CountryRule countryRule, boolean isValid, long startTime) {
        
        PhoneValidationResult.PhoneValidationResultBuilder builder = PhoneValidationResult.builder()
                .originalNumber(originalNumber)
                .standardizedNumber(formatToE164(cleanNumber, countryRule))
                .status(isValid ? PhoneValidationResult.ValidationStatus.VALID : PhoneValidationResult.ValidationStatus.INVALID)
                .isValid(isValid)
                .validatedTime(new java.util.Date())
                .validationDuration(System.currentTimeMillis() - startTime);
        
        if (countryRule != null) {
            builder.countryCode(countryRule.countryCode)
                   .countryName(countryRule.countryName)
                   .numberType(determineNumberType(cleanNumber, countryRule))
                   .isMobile(isMobileNumber(cleanNumber, countryRule))
                   .canReceiveSms(isMobileNumber(cleanNumber, countryRule));
        }
        
        // 设置运营商信息（基于号段）
        if (isValid && countryRule != null) {
            builder.carrierInfo(detectCarrier(cleanNumber, countryRule));
        }
        
        // 设置风险评估
        builder.riskAssessment(PhoneValidationResult.RiskAssessment.builder()
                .riskLevel(PhoneValidationResult.RiskLevel.LOW)
                .riskScore(10)
                .isDisposable(false)
                .isVirtual(false)
                .riskDescription("本地校验，风险评估有限")
                .build());
        
        return builder.build();
    }
    
    /**
     * 格式化为E.164格式
     */
    private String formatToE164(String cleanNumber, CountryRule countryRule) {
        if (countryRule == null) return "+" + cleanNumber;
        
        // 如果号码已包含国家代码，直接添加+
        if (cleanNumber.startsWith(countryRule.countryCode)) {
            return "+" + cleanNumber;
        }
        
        // 否则添加国家代码
        return "+" + countryRule.countryCode + cleanNumber;
    }
    
    /**
     * 判断号码类型
     */
    private PhoneValidationResult.NumberType determineNumberType(String cleanNumber, CountryRule countryRule) {
        if (isMobileNumber(cleanNumber, countryRule)) {
            return PhoneValidationResult.NumberType.MOBILE;
        }
        return PhoneValidationResult.NumberType.UNKNOWN;
    }
    
    /**
     * 判断是否为移动号码
     */
    private boolean isMobileNumber(String cleanNumber, CountryRule countryRule) {
        if (countryRule.mobilePattern == null) return true; // 默认认为是移动号码
        
        return countryRule.mobilePattern.matcher(cleanNumber).matches();
    }
    
    /**
     * 检测运营商
     */
    private PhoneValidationResult.CarrierInfo detectCarrier(String cleanNumber, CountryRule countryRule) {
        // 基于号段检测运营商（以中国为例）
        if ("86".equals(countryRule.countryCode)) {
            return detectChineseCarrier(cleanNumber);
        }
        
        return PhoneValidationResult.CarrierInfo.builder()
                .name("未知运营商")
                .networkType("Mobile")
                .build();
    }
    
    /**
     * 检测中国运营商
     */
    private PhoneValidationResult.CarrierInfo detectChineseCarrier(String cleanNumber) {
        String prefix = cleanNumber.length() >= 11 ? cleanNumber.substring(0, 3) : "";
        
        // 中国移动号段
        if (prefix.matches("^(134|135|136|137|138|139|147|150|151|152|157|158|159|178|182|183|184|187|188|198).*")) {
            return PhoneValidationResult.CarrierInfo.builder()
                    .name("中国移动")
                    .code("CMCC")
                    .mcc("460")
                    .mnc("00")
                    .networkType("GSM")
                    .build();
        }
        // 中国联通号段
        else if (prefix.matches("^(130|131|132|145|155|156|166|175|176|185|186).*")) {
            return PhoneValidationResult.CarrierInfo.builder()
                    .name("中国联通")
                    .code("CUCC")
                    .mcc("460")
                    .mnc("01")
                    .networkType("GSM")
                    .build();
        }
        // 中国电信号段
        else if (prefix.matches("^(133|149|153|173|177|180|181|189|199).*")) {
            return PhoneValidationResult.CarrierInfo.builder()
                    .name("中国电信")
                    .code("CTCC")
                    .mcc("460")
                    .mnc("03")
                    .networkType("CDMA")
                    .build();
        }
        
        return PhoneValidationResult.CarrierInfo.builder()
                .name("未知运营商")
                .networkType("Mobile")
                .build();
    }
    
    /**
     * 创建错误结果
     */
    private PhoneValidationResult createErrorResult(String originalNumber, String errorMessage, long startTime) {
        return PhoneValidationResult.builder()
                .originalNumber(originalNumber)
                .status(PhoneValidationResult.ValidationStatus.ERROR)
                .isValid(false)
                .errorMessage(errorMessage)
                .validatedTime(new java.util.Date())
                .validationDuration(System.currentTimeMillis() - startTime)
                .build();
    }
    
    /**
     * 创建通用国家规则 - 使用宽泛的统一规则
     */
    private CountryRule createGenericCountryRule() {
        // 使用统一的宽泛规则：4-18位数字且不以0开头
        Pattern genericPattern = Pattern.compile("^[1-9]\\d{3,17}$");
        return new CountryRule(
                "Generic", "通用", 
                genericPattern, 
                genericPattern
        );
    }
    
    /**
     * 初始化国家规则 - 统一为宽泛规则
     */
    private static void initializeCountryRules() {
        // 通用规则：4-18位数字且不以0开头
        Pattern unifiedPattern = Pattern.compile("^[1-9]\\d{3,17}$");
        
        // 中国 - 使用统一规则
        COUNTRY_RULES.put("86", new CountryRule(
                "86", "中国",
                unifiedPattern,
                unifiedPattern
        ));
        
        // 美国 - 使用统一规则，解决13394145483被误识别的问题
        COUNTRY_RULES.put("1", new CountryRule(
                "1", "美国",
                unifiedPattern,
                unifiedPattern
        ));
        
        // 英国 - 使用统一规则
        COUNTRY_RULES.put("44", new CountryRule(
                "44", "英国",
                unifiedPattern,
                unifiedPattern
        ));
        
        // 日本 - 使用统一规则
        COUNTRY_RULES.put("81", new CountryRule(
                "81", "日本",
                unifiedPattern,
                unifiedPattern
        ));
        
        // 韩国 - 使用统一规则
        COUNTRY_RULES.put("82", new CountryRule(
                "82", "韩国",
                unifiedPattern,
                unifiedPattern
        ));
        
        // 可以继续添加更多国家，都使用相同的统一规则
    }
    
    @Override
    public String getSupportedProvider() {
        return "LOCAL";
    }
    
    @Override
    public boolean supportsBatchValidation() {
        return true;
    }
    
    @Override
    public String getCostInfo() {
        return "完全免费";
    }
    
    @Override
    public String getLimitInfo() {
        return "无限制，本地校验";
    }
    
    /**
     * 国家规则内部类
     */
    private static class CountryRule {
        final String countryCode;
        final String countryName;
        final Pattern pattern;
        final Pattern mobilePattern;
        
        CountryRule(String countryCode, String countryName, Pattern pattern, Pattern mobilePattern) {
            this.countryCode = countryCode;
            this.countryName = countryName;
            this.pattern = pattern;
            this.mobilePattern = mobilePattern;
        }
    }
} 