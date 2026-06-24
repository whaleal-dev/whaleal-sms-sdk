package com.whaleal.ark.cloud.third.sms.client;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.util.TextUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信发送凭证（运行时动态传入，不写入配置文件）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsCredentials {

    private String apiKey;
    private String apiSecret;
    private String accessKeyId;
    private String accessKeySecret;

    public boolean hasAuth() {
        return TextUtils.hasText(apiKey)
                || TextUtils.hasText(apiSecret)
                || TextUtils.hasText(accessKeyId)
                || TextUtils.hasText(accessKeySecret);
    }

    /**
     * 将凭证合并到基础配置
     */
    public SmsProviderConfig mergeWith(SmsProviderConfig base) {
        if (base == null) {
            base = SmsProviderConfig.builder().build();
        }
        return base.toBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build();
    }
}
