package com.whaleal.ark.cloud.third.sms.exception;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;

/**
 * SMS 配额限制异常
 * 当达到速率限制、余额不足或配额耗尽时抛出
 * 
 */
public class SmsQuotaException extends SmsException {
    
    /**
     * 当前配额
     */
    private final Long currentQuota;
    
    /**
     * 配额限制
     */
    private final Long quotaLimit;
    
    /**
     * 重试时间（秒）
     */
    private final Integer retryAfterSeconds;
    
    public SmsQuotaException(String message) {
        super("QUOTA_EXCEEDED", message);
        this.currentQuota = null;
        this.quotaLimit = null;
        this.retryAfterSeconds = null;
    }
    
    public SmsQuotaException(String message, SmsProviderType providerType) {
        super("QUOTA_EXCEEDED", message, providerType);
        this.currentQuota = null;
        this.quotaLimit = null;
        this.retryAfterSeconds = null;
    }
    
    public SmsQuotaException(String message, SmsProviderType providerType, Long currentQuota, Long quotaLimit, Integer retryAfterSeconds) {
        super("QUOTA_EXCEEDED", message, providerType);
        this.currentQuota = currentQuota;
        this.quotaLimit = quotaLimit;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public Long getCurrentQuota() {
        return currentQuota;
    }
    
    public Long getQuotaLimit() {
        return quotaLimit;
    }
    
    public Integer getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
    
    /**
     * 速率限制
     */
    public static SmsQuotaException rateLimitExceeded(SmsProviderType providerType, int retryAfterSeconds) {
        return new SmsQuotaException(
                String.format("速率限制超出，请在 %d 秒后重试", retryAfterSeconds), 
                providerType, 
                null, 
                null, 
                retryAfterSeconds
        );
    }
    
    /**
     * 余额不足
     */
    public static SmsQuotaException insufficientBalance(SmsProviderType providerType, double currentBalance, double requiredAmount) {
        return new SmsQuotaException(
                String.format("余额不足: 当前余额 %.2f，需要 %.2f", currentBalance, requiredAmount), 
                providerType
        );
    }
    
    /**
     * 日配额耗尽
     */
    public static SmsQuotaException dailyQuotaExceeded(SmsProviderType providerType, long currentUsage, long dailyLimit) {
        return new SmsQuotaException(
                String.format("日配额已耗尽: 已使用 %d，日限制 %d", currentUsage, dailyLimit), 
                providerType, 
                currentUsage, 
                dailyLimit, 
                null
        );
    }
    
    /**
     * 月配额耗尽
     */
    public static SmsQuotaException monthlyQuotaExceeded(SmsProviderType providerType, long currentUsage, long monthlyLimit) {
        return new SmsQuotaException(
                String.format("月配额已耗尽: 已使用 %d，月限制 %d", currentUsage, monthlyLimit), 
                providerType, 
                currentUsage, 
                monthlyLimit, 
                null
        );
    }
    
    /**
     * 并发限制
     */
    public static SmsQuotaException concurrencyLimitExceeded(SmsProviderType providerType, int currentConcurrency, int maxConcurrency) {
        return new SmsQuotaException(
                String.format("并发限制超出: 当前并发 %d，最大并发 %d", currentConcurrency, maxConcurrency), 
                providerType
        );
    }
    
    /**
     * 账户被暂停
     */
    public static SmsQuotaException accountSuspended(SmsProviderType providerType, String reason) {
        return new SmsQuotaException(
                String.format("账户已被暂停: %s", reason), 
                providerType
        );
    }
    
    /**
     * 分钟级频率限制
     */
    public static SmsQuotaException minuteRateLimitExceeded(SmsProviderType providerType, int currentCount, int minuteLimit, int retryAfterSeconds) {
        return new SmsQuotaException(
                String.format("分钟频率限制超出: 已发送 %d 条，分钟限制 %d 条", currentCount, minuteLimit), 
                providerType, 
                (long)currentCount, 
                (long)minuteLimit, 
                retryAfterSeconds
        );
    }
    
    /**
     * 小时级频率限制
     */
    public static SmsQuotaException hourRateLimitExceeded(SmsProviderType providerType, int currentCount, int hourLimit, int retryAfterSeconds) {
        return new SmsQuotaException(
                String.format("小时频率限制超出: 已发送 %d 条，小时限制 %d 条", currentCount, hourLimit), 
                providerType, 
                (long)currentCount, 
                (long)hourLimit, 
                retryAfterSeconds
        );
    }
    
    /**
     * 单个号码频率限制
     */
    public static SmsQuotaException phoneNumberRateLimitExceeded(String phoneNumber, SmsProviderType providerType, int currentCount, int dailyLimit) {
        return new SmsQuotaException(
                String.format("号码 %s 日发送频率超限: 已发送 %d 条，日限制 %d 条", phoneNumber, currentCount, dailyLimit), 
                providerType, 
                (long)currentCount, 
                (long)dailyLimit, 
                null
        );
    }
    
    /**
     * IP频率限制
     */
    public static SmsQuotaException ipRateLimitExceeded(String ipAddress, SmsProviderType providerType, int retryAfterSeconds) {
        return new SmsQuotaException(
                String.format("IP %s 请求频率超限", ipAddress), 
                providerType, 
                null, 
                null, 
                retryAfterSeconds
        );
    }
    
    /**
     * 预付费余额不足
     */
    public static SmsQuotaException prepaidBalanceInsufficient(SmsProviderType providerType, double currentBalance, double requiredAmount, String currency) {
        return new SmsQuotaException(
                String.format("预付费余额不足: 当前余额 %.4f %s，需要 %.4f %s", currentBalance, currency, requiredAmount, currency), 
                providerType
        );
    }
    
    /**
     * 后付费信用额度不足
     */
    public static SmsQuotaException postpaidCreditInsufficient(SmsProviderType providerType, double currentCredit, double requiredAmount, String currency) {
        return new SmsQuotaException(
                String.format("后付费信用额度不足: 当前额度 %.4f %s，需要 %.4f %s", currentCredit, currency, requiredAmount, currency), 
                providerType
        );
    }
    
    /**
     * 账户欠费
     */
    public static SmsQuotaException accountOverdue(SmsProviderType providerType, double overdueAmount, String currency) {
        return new SmsQuotaException(
                String.format("账户欠费: 欠费金额 %.4f %s，请及时充值", overdueAmount, currency), 
                providerType
        );
    }
    
    /**
     * 免费配额耗尽
     */
    public static SmsQuotaException freeQuotaExhausted(SmsProviderType providerType, int usedCount, int freeLimit) {
        return new SmsQuotaException(
                String.format("免费配额已耗尽: 已使用 %d 条，免费额度 %d 条", usedCount, freeLimit), 
                providerType, 
                (long)usedCount, 
                (long)freeLimit, 
                null
        );
    }
    
    /**
     * 套餐短信数量不足
     */
    public static SmsQuotaException packageSmsInsufficient(SmsProviderType providerType, int remainingCount, int requiredCount) {
        return new SmsQuotaException(
                String.format("套餐短信数量不足: 剩余 %d 条，需要 %d 条", remainingCount, requiredCount), 
                providerType, 
                (long)remainingCount, 
                null, 
                null
        );
    }
    
    @Override
    public String getFullErrorMessage() {
        String baseMessage = super.getFullErrorMessage();
        if (currentQuota != null && quotaLimit != null) {
            baseMessage += String.format(" (配额: %d/%d)", currentQuota, quotaLimit);
        }
        if (retryAfterSeconds != null) {
            baseMessage += String.format(" (重试间隔: %d秒)", retryAfterSeconds);
        }
        return baseMessage;
    }
} 