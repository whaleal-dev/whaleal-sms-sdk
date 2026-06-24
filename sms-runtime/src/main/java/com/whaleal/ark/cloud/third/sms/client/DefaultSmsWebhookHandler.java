package com.whaleal.ark.cloud.third.sms.client;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.core.SmsModuleManager;
import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInboundMessage;
import com.whaleal.ark.cloud.third.sms.receipt.entity.SmsReceipt;
import com.whaleal.ark.cloud.third.sms.report.entity.SmsReport;
import com.whaleal.ark.cloud.third.sms.validation.PhoneValidationAdapter;
import com.whaleal.ark.cloud.third.sms.validation.entity.PhoneValidationResult;

import java.util.List;
import java.util.Map;

/**
 * {@link SmsWebhookHandler} 默认实现
 */
public class DefaultSmsWebhookHandler implements SmsWebhookHandler {

    private final SmsModuleManager moduleManager;
    private final PhoneValidationAdapter phoneValidationAdapter;
    private final SmsProviderConfig baseConfig;

    public DefaultSmsWebhookHandler(SmsModuleManager moduleManager,
                                    PhoneValidationAdapter phoneValidationAdapter,
                                    SmsProviderConfig baseConfig) {
        this.moduleManager = moduleManager;
        this.phoneValidationAdapter = phoneValidationAdapter;
        this.baseConfig = baseConfig;
    }

    @Override
    public SmsReceipt parseReceipt(SmsProviderType provider, Map<String, Object> payload) {
        return moduleManager.parseReceipt(provider, payload, baseConfig);
    }

    @Override
    public SmsReceipt parseReceipt(Map<String, Object> payload) {
        return moduleManager.parseReceipt(payload, baseConfig);
    }

    @Override
    public SmsInboundMessage parseInbound(SmsProviderType provider, Map<String, Object> payload) {
        return moduleManager.parseInbound(provider, payload, baseConfig);
    }

    @Override
    public SmsInboundMessage parseInbound(Map<String, Object> payload) {
        return moduleManager.parseInbound(payload, baseConfig);
    }

    @Override
    public SmsReport fetchReport(SmsProviderType provider, String messageId, SmsCredentials credentials) {
        return moduleManager.fetchReport(provider, messageId, withCredentials(credentials));
    }

    @Override
    public List<SmsReport> fetchReports(SmsProviderType provider, List<String> messageIds, SmsCredentials credentials) {
        return moduleManager.fetchReports(provider, messageIds, withCredentials(credentials));
    }

    @Override
    public PhoneValidationResult validatePhone(SmsProviderType provider, String phoneNumber, SmsCredentials credentials) {
        return phoneValidationAdapter.validate(provider, phoneNumber, withCredentials(credentials));
    }

    @Override
    public List<PhoneValidationResult> validatePhones(SmsProviderType provider, List<String> phoneNumbers, SmsCredentials credentials) {
        return phoneValidationAdapter.validateBatch(provider, phoneNumbers, withCredentials(credentials));
    }

    @Override
    public SmsProviderConfig getBaseConfig() {
        return baseConfig;
    }

    private SmsProviderConfig withCredentials(SmsCredentials credentials) {
        if (credentials == null) {
            return baseConfig;
        }
        return credentials.mergeWith(baseConfig);
    }
}
