package com.whaleal.ark.cloud.third.sms.client;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 批量发送请求，支持 1对1、1对多、多对多。
 * <p>
 * 使用 {@link #oneToOne(SmsSendRequest)}、{@link #oneToMany(List, String)}、
 * {@link #manyToMany(List)} 等工厂方法创建。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsBatchSendRequest {

    private SmsProviderType provider;
    private SmsCredentials credentials;
    private String from;
    private String content;
    private String templateId;
    private Map<String, String> templateParams;
    private String referenceId;

    /** 1对1 / 1对多：接收方号码列表 */
    private List<String> recipients;

    /** 多对多：每条独立请求 */
    private List<SmsSendRequest> items;

    public static SmsBatchSendRequest oneToOne(SmsSendRequest request) {
        if (request == null) {
            return new SmsBatchSendRequest();
        }
        return SmsBatchSendRequest.builder()
                .provider(request.getProvider())
                .credentials(request.getCredentials())
                .from(request.getFrom())
                .content(request.getContent())
                .templateId(request.getTemplateId())
                .templateParams(request.getTemplateParams())
                .referenceId(request.getReferenceId())
                .recipients(request.getTo() != null ? List.of(request.getTo()) : null)
                .build();
    }

    public static SmsBatchSendRequest oneToMany(List<String> recipients, String content) {
        return SmsBatchSendRequest.builder()
                .recipients(recipients)
                .content(content)
                .build();
    }

    public static SmsBatchSendRequest oneToMany(SmsSendRequest template, List<String> recipients) {
        if (template == null) {
            return oneToMany(recipients, null);
        }
        return SmsBatchSendRequest.builder()
                .provider(template.getProvider())
                .credentials(template.getCredentials())
                .from(template.getFrom())
                .content(template.getContent())
                .templateId(template.getTemplateId())
                .templateParams(template.getTemplateParams())
                .referenceId(template.getReferenceId())
                .recipients(recipients)
                .build();
    }

    public static SmsBatchSendRequest manyToMany(List<SmsSendRequest> items) {
        return SmsBatchSendRequest.builder()
                .items(items)
                .build();
    }

    public SmsSendMode resolveMode() {
        if (items != null && !items.isEmpty()) {
            return SmsSendMode.MANY_TO_MANY;
        }
        if (recipients != null && recipients.size() > 1) {
            return SmsSendMode.ONE_TO_MANY;
        }
        return SmsSendMode.ONE_TO_ONE;
    }

    public List<SmsSendRequest> toSendRequests() {
        if (items != null && !items.isEmpty()) {
            return new ArrayList<>(items);
        }
        if (recipients == null || recipients.isEmpty()) {
            return Collections.emptyList();
        }
        List<SmsSendRequest> requests = new ArrayList<>(recipients.size());
        for (String recipient : recipients) {
            requests.add(SmsSendRequest.builder()
                    .to(recipient)
                    .provider(provider)
                    .credentials(credentials)
                    .from(from)
                    .content(content)
                    .templateId(templateId)
                    .templateParams(templateParams)
                    .referenceId(referenceId)
                    .build());
        }
        return requests;
    }

    boolean canUseNativeBatch() {
        return resolveMode() == SmsSendMode.ONE_TO_MANY
                && items == null
                && recipients != null
                && recipients.size() > 1;
    }
}
