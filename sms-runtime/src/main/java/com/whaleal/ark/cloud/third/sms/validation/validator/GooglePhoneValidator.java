package com.whaleal.ark.cloud.third.sms.validation.validator;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.validation.entity.PhoneValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

/**
 * Google libphonenumber 校验器 - 免费的高精度校验
 * 基于Google开源的libphonenumber库进行校验
 * 
 * 注意：需要添加依赖：
 * <dependency>
 *     <groupId>com.googlecode.libphonenumber</groupId>
 *     <artifactId>libphonenumber</artifactId>
 *     <version>8.13.20</version>
 * </dependency>
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class GooglePhoneValidator implements PhoneValidator {
    
    private com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil;
    private boolean libAvailable = false;
    
    public GooglePhoneValidator() {
        try {
            phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
            libAvailable = true;
            log.info("Google libphonenumber库加载成功（基础验证功能）");
        } catch (Exception e) {
            log.error("Google libphonenumber不可用: {}", e.getMessage());
            libAvailable = false;
        }
    }
    
    @Override
    public PhoneValidationResult validate(String phoneNumber, SmsProviderConfig config) {
        long startTime = System.currentTimeMillis();
        
        if (!libAvailable || phoneUtil == null) {
            log.debug("Google libphonenumber不可用，使用模拟校验: {}", phoneNumber);
            return createMockResult(phoneNumber, startTime);
        }
        
        try {
            log.debug("开始Google校验号码: {}", phoneNumber);
            
            // 预处理号码：客户号码都是国际格式，如果不以+开头，自动添加+号
            String formattedNumber = phoneNumber;
            if (phoneNumber != null && !phoneNumber.startsWith("+") && phoneNumber.matches("^\\d+$")) {
                formattedNumber = "+" + phoneNumber;
                log.debug("自动添加+号（国际格式）: {} -> {}", phoneNumber, formattedNumber);
            }
            
            // 解析号码
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(formattedNumber, null);
            
            // 校验号码
            boolean isValid = phoneUtil.isValidNumber(parsedNumber);
            boolean isPossible = phoneUtil.isPossibleNumber(parsedNumber);
            
            // 获取号码类型
            com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType numberType = phoneUtil.getNumberType(parsedNumber);
            
            // 获取地理位置信息
            String regionCode = phoneUtil.getRegionCodeForNumber(parsedNumber);
            
            // 构建结果（暂时不包含高级功能：地理编码和运营商信息）
            return buildGoogleResult(phoneNumber, parsedNumber, isValid, isPossible, 
                                   numberType, regionCode, null, null, startTime);
            
        } catch (Exception e) {
            log.error("Google号码校验失败: {}", phoneNumber, e);
            return createErrorResult(phoneNumber, e.getMessage(), startTime);
        }
    }
    
    /**
     * 构建Google验证结果
     */
    private PhoneValidationResult buildGoogleResult(String phoneNumber, 
                                                   com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsedNumber,
                                                   boolean isValid, boolean isPossible,
                                                   com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType numberType,
                                                   String regionCode, String locationName, String carrier,
                                                   long startTime) {
        
        PhoneValidationResult.ValidationStatus status;
        if (isValid) {
            status = PhoneValidationResult.ValidationStatus.VALID;
        } else if (isPossible) {
            status = PhoneValidationResult.ValidationStatus.UNKNOWN;
        } else {
            status = PhoneValidationResult.ValidationStatus.INVALID;
        }
        
        // 构建位置信息
        PhoneValidationResult.LocationInfo locationInfo = null;
        if (regionCode != null && !regionCode.trim().isEmpty()) {
            locationInfo = PhoneValidationResult.LocationInfo.builder()
                    .country(regionCode)
                    .state(locationName != null ? locationName : getCountryName(regionCode))
                    .build();
        }
        
        // 构建运营商信息
        PhoneValidationResult.CarrierInfo carrierInfo = null;
        if (carrier != null && !carrier.isEmpty()) {
            carrierInfo = PhoneValidationResult.CarrierInfo.builder()
                    .name(carrier)
                    .build();
        }
        
        // 构建号码类型
        PhoneValidationResult.NumberType resultNumberType = mapNumberType(numberType);
        
        return PhoneValidationResult.builder()
                .originalNumber(phoneNumber)
                .countryCode(String.valueOf(parsedNumber.getCountryCode()))
                .countryName(getCountryName(regionCode))
                .standardizedNumber(phoneUtil.format(parsedNumber, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164))
                .status(status)
                .isValid(isValid)
                .isMobile(resultNumberType == PhoneValidationResult.NumberType.MOBILE)
                .canReceiveSms(resultNumberType == PhoneValidationResult.NumberType.MOBILE)
                .numberType(resultNumberType)
                .locationInfo(locationInfo)
                .carrierInfo(carrierInfo)
                .validationDuration(System.currentTimeMillis() - startTime)
                .validatedTime(new java.util.Date())
                .build();
    }
    
    /**
     * 获取国家名称
     */
    private String getCountryName(String regionCode) {
        // 处理null值
        if (regionCode == null || regionCode.trim().isEmpty()) {
            return "未知地区";
        }
        
        // 简单的国家名称映射
        return switch (regionCode.trim().toUpperCase()) {
            case "CN" -> "中国";
            case "US" -> "美国";
            case "GB" -> "英国";
            case "JP" -> "日本";
            case "KR" -> "韩国";
            case "SG" -> "新加坡";
            case "MY" -> "马来西亚";
            case "TH" -> "泰国";
            case "VN" -> "越南";
            case "ID" -> "印度尼西亚";
            case "PH" -> "菲律宾";
            case "IN" -> "印度";
            case "AU" -> "澳大利亚";
            case "CA" -> "加拿大";
            case "DE" -> "德国";
            case "FR" -> "法国";
            case "IT" -> "意大利";
            case "RU" -> "俄罗斯";
            case "BR" -> "巴西";
            case "HK" -> "香港";
            case "TW" -> "台湾";
            case "MO" -> "澳门";
            default -> regionCode;
        };
    }
    
    /**
     * 映射号码类型
     */
    private PhoneValidationResult.NumberType mapNumberType(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType googleType) {
        switch (googleType) {
            case MOBILE:
            case FIXED_LINE_OR_MOBILE:
                return PhoneValidationResult.NumberType.MOBILE;
            case FIXED_LINE:
                return PhoneValidationResult.NumberType.FIXED_LINE;
            case TOLL_FREE:
                return PhoneValidationResult.NumberType.TOLL_FREE;
            case PREMIUM_RATE:
                return PhoneValidationResult.NumberType.PREMIUM_RATE;
            case VOIP:
                return PhoneValidationResult.NumberType.VOIP;
            default:
                return PhoneValidationResult.NumberType.UNKNOWN;
        }
    }
    
    /**
     * 创建模拟结果（当libphonenumber依赖不可用时）
     */
    private PhoneValidationResult createMockResult(String phoneNumber, long startTime) {
        // 简单的格式检查 - 与前端校验规则保持一致：4-18位数字，不能以0开头
        String cleanNumber = phoneNumber != null ? phoneNumber.replaceAll("[^0-9]", "") : "";
        boolean isValid = cleanNumber.length() >= 4 && cleanNumber.length() <= 18 && 
                         !cleanNumber.startsWith("0") && cleanNumber.matches("^[1-9]\\d{3,17}$");
        
        return PhoneValidationResult.builder()
                .originalNumber(phoneNumber)
                .standardizedNumber(phoneNumber)
                .status(isValid ? PhoneValidationResult.ValidationStatus.VALID : 
                               PhoneValidationResult.ValidationStatus.INVALID)
                .isValid(isValid)
                .canReceiveSms(isValid)
                .isMobile(true)
                .numberType(PhoneValidationResult.NumberType.MOBILE)
                .countryCode("86")
                .countryName("中国")
                .validatedTime(new java.util.Date())
                .validationDuration(System.currentTimeMillis() - startTime)
                .errorMessage("Google libphonenumber依赖未添加，使用模拟校验")
                .riskAssessment(PhoneValidationResult.RiskAssessment.builder()
                        .riskLevel(PhoneValidationResult.RiskLevel.LOW)
                        .riskScore(20)
                        .isDisposable(false)
                        .isVirtual(false)
                        .riskDescription("模拟校验结果")
                        .build())
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
    
    @Override
    public String getSupportedProvider() {
        return "GOOGLE_LIBPHONENUMBER";
    }
    
    @Override
    public boolean supportsBatchValidation() {
        return true;
    }
    
    @Override
    public String getCostInfo() {
        return "完全免费（开源库）";
    }
    
    @Override
    public String getLimitInfo() {
        return "无限制，本地处理";
    }
} 