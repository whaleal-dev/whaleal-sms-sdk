package com.whaleal.ark.cloud.third.sms.validation.example;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import com.whaleal.ark.cloud.third.sms.validation.PhoneValidationAdapter;
import com.whaleal.ark.cloud.third.sms.validation.entity.PhoneValidationResult;

import java.util.Arrays;
import java.util.List;

/**
 * 号码校验功能使用示例
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
public class PhoneValidationExample {
    
    public static void main(String[] args) {
        // 创建校验适配器
        PhoneValidationAdapter validator = new PhoneValidationAdapter();
        
        System.out.println("=== SMS SDK 号码校验功能演示 ===\n");
        
        // 1. 基础本地校验（免费）
        demonstrateLocalValidation(validator);
        
        // 2. Twilio校验（付费）
        demonstrateTwilioValidation(validator);
        
        // 3. Vonage校验（基础免费，高级付费）
        demonstrateVonageValidation(validator);
        
        // 4. 智能校验
        demonstrateSmartValidation(validator);
        
        // 5. 批量校验
        demonstrateBatchValidation(validator);
        
        // 6. 快速格式校验
        demonstrateQuickValidation(validator);
        
        // 7. 显示支持的校验器
        demonstrateSupportedValidators(validator);
    }
    
    /**
     * 演示本地校验
     */
    private static void demonstrateLocalValidation(PhoneValidationAdapter validator) {
        System.out.println("1. 本地校验演示（免费）");
        System.out.println("----------------------------------------");
        
        String[] testNumbers = {
            "+8613800138000",    // 中国移动
            "13800138000",       // 中国移动（无国际前缀）
            "+8618600186000",    // 中国联通
            "+8618900189000",    // 中国电信
            "+12125551234",      // 美国号码
            "invalid-number"     // 无效号码
        };
        
        for (String phoneNumber : testNumbers) {
            // 使用统一的参数顺序：(providerType, phoneNumber, config)
            PhoneValidationResult result = validator.validate(SmsProviderType.MOCK, phoneNumber, null);
            printValidationResult(phoneNumber, result);
        }
        System.out.println();
    }
    
    /**
     * 演示Twilio校验
     */
    private static void demonstrateTwilioValidation(PhoneValidationAdapter validator) {
        System.out.println("2. Twilio校验演示（付费）");
        System.out.println("----------------------------------------");
        
        // 创建Twilio配置（需要真实的Account SID和Auth Token）
        SmsProviderConfig twilioConfig = SmsProviderConfig.builder()
                .apiKey("your_twilio_account_sid")
                .apiSecret("your_twilio_auth_token")
                .build();
        
        String phoneNumber = "+8613800138000";
        // 使用统一的参数顺序：(providerType, phoneNumber, config)
        PhoneValidationResult result = validator.validate(SmsProviderType.TWILIO, phoneNumber, twilioConfig);
        
        System.out.println("注意：这需要真实的Twilio凭据才能工作");
        printValidationResult(phoneNumber, result);
        System.out.println();
    }
    
    /**
     * 演示Vonage校验
     */
    private static void demonstrateVonageValidation(PhoneValidationAdapter validator) {
        System.out.println("3. Vonage校验演示（基础免费，高级付费）");
        System.out.println("----------------------------------------");
        
        // 创建Vonage配置（需要真实的API Key和Secret）
        SmsProviderConfig vonageConfig = SmsProviderConfig.builder()
                .apiKey("your_vonage_api_key")
                .apiSecret("your_vonage_api_secret")
                .build();
        
        String phoneNumber = "+8613800138000";
        // 使用统一的参数顺序：(providerType, phoneNumber, config)
        PhoneValidationResult result = validator.validate(SmsProviderType.VONAGE, phoneNumber, vonageConfig);
        
        System.out.println("注意：这需要真实的Vonage凭据才能工作");
        printValidationResult(phoneNumber, result);
        System.out.println();
    }
    
    /**
     * 演示智能校验
     */
    private static void demonstrateSmartValidation(PhoneValidationAdapter validator) {
        System.out.println("4. 智能校验演示（自动选择最佳校验器）");
        System.out.println("----------------------------------------");
        
        String phoneNumber = "+8613800138000";
        
        // 不提供配置，只使用免费校验器
        PhoneValidationResult result1 = validator.smartValidate(phoneNumber, null);
        System.out.println("无配置的智能校验：");
        printValidationResult(phoneNumber, result1);
        
        // 提供配置，可能使用付费校验器获取更详细信息
        SmsProviderConfig config = SmsProviderConfig.builder()
                .apiKey("your_api_key")
                .apiSecret("your_api_secret")
                .build();
        
        PhoneValidationResult result2 = validator.smartValidate(phoneNumber, config);
        System.out.println("有配置的智能校验：");
        printValidationResult(phoneNumber, result2);
        System.out.println();
    }
    
    /**
     * 演示批量校验
     */
    private static void demonstrateBatchValidation(PhoneValidationAdapter validator) {
        System.out.println("5. 批量校验演示");
        System.out.println("----------------------------------------");
        
        List<String> phoneNumbers = Arrays.asList(
            "+8613800138000",
            "+8618600186000", 
            "+8618900189000",
            "+12125551234",
            "invalid-number"
        );
        
        // 使用统一的参数顺序：(providerType, phoneNumbers, config)
        List<PhoneValidationResult> results = validator.validateBatch(SmsProviderType.MOCK, phoneNumbers, null);
        
        System.out.println("批量校验结果：");
        for (int i = 0; i < phoneNumbers.size(); i++) {
            System.out.printf("  %d. %s -> %s\n", 
                i + 1, 
                phoneNumbers.get(i), 
                results.get(i).getStatus()
            );
        }
        System.out.println();
    }
    
    /**
     * 演示快速格式校验
     */
    private static void demonstrateQuickValidation(PhoneValidationAdapter validator) {
        System.out.println("6. 快速格式校验演示");
        System.out.println("----------------------------------------");
        
        String[] testNumbers = {
            "+8613800138000",
            "13800138000",
            "+12125551234",
            "invalid-number",
            "123"
        };
        
        for (String phoneNumber : testNumbers) {
            boolean isValid = validator.isValidFormat(phoneNumber);
            System.out.printf("  %s -> %s\n", phoneNumber, isValid ? "有效格式" : "无效格式");
        }
        System.out.println();
    }
    
    /**
     * 演示支持的校验器
     */
    private static void demonstrateSupportedValidators(PhoneValidationAdapter validator) {
        System.out.println("7. 支持的校验器列表");
        System.out.println("----------------------------------------");
        
        List<PhoneValidationAdapter.ValidatorInfo> validators = validator.getSupportedValidators();
        
        for (PhoneValidationAdapter.ValidatorInfo info : validators) {
            System.out.printf("校验器: %s\n", info.getName());
            System.out.printf("  提供商: %s\n", info.getProvider());
            System.out.printf("  费用: %s\n", info.getCostInfo());
            System.out.printf("  限制: %s\n", info.getLimitInfo());
            System.out.printf("  批量支持: %s\n", info.isSupportsBatch() ? "是" : "否");
            System.out.println();
        }
    }
    
    /**
     * 打印校验结果
     */
    private static void printValidationResult(String phoneNumber, PhoneValidationResult result) {
        System.out.printf("号码: %s\n", phoneNumber);
        System.out.printf("  状态: %s\n", result.getStatus());
        System.out.printf("  有效: %s\n", result.getIsValid());
        System.out.printf("  标准化: %s\n", result.getStandardizedNumber());
        System.out.printf("  国家: %s (%s)\n", result.getCountryName(), result.getCountryCode());
        System.out.printf("  类型: %s\n", result.getNumberType());
        System.out.printf("  移动号码: %s\n", result.getIsMobile());
        System.out.printf("  可接收短信: %s\n", result.getCanReceiveSms());
        
        if (result.getCarrierInfo() != null) {
            System.out.printf("  运营商: %s\n", result.getCarrierInfo().getName());
        }
        
        if (result.getRiskAssessment() != null) {
            System.out.printf("  风险等级: %s (评分: %d)\n", 
                result.getRiskAssessment().getRiskLevel(),
                result.getRiskAssessment().getRiskScore());
        }
        
        if (result.getValidationDuration() != null) {
            System.out.printf("  耗时: %dms\n", result.getValidationDuration());
        }
        
        if (result.getErrorMessage() != null) {
            System.out.printf("  错误: %s\n", result.getErrorMessage());
        }
        
        System.out.println();
    }
} 