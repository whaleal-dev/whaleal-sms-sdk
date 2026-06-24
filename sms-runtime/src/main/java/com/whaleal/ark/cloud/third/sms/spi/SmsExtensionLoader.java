package com.whaleal.ark.cloud.third.sms.spi;

import com.whaleal.ark.cloud.third.sms.enums.SmsProviderType;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * 通过 Java SPI 加载各供应商扩展实现。
 */
@Slf4j
public final class SmsExtensionLoader {

    private SmsExtensionLoader() {
    }

    public static <T> Map<SmsProviderType, T> loadProviders(Class<T> serviceType,
                                                             Function<T, String> providerNameExtractor) {
        Map<SmsProviderType, T> providers = new HashMap<>();
        ServiceLoader<T> loader = ServiceLoader.load(serviceType, Thread.currentThread().getContextClassLoader());
        for (T implementation : loader) {
            try {
                SmsProviderType providerType = resolveProviderType(providerNameExtractor.apply(implementation));
                T existing = providers.put(providerType, implementation);
                if (existing != null) {
                    log.warn("供应商 {} 存在多个 {} 实现，使用后加载的实现: {}",
                            providerType, serviceType.getSimpleName(), implementation.getClass().getName());
                } else {
                    log.debug("已加载 {} -> {}", providerType, implementation.getClass().getName());
                }
            } catch (IllegalArgumentException ex) {
                log.warn("跳过无法识别的 {} 实现 {}: {}",
                        serviceType.getSimpleName(), implementation.getClass().getName(), ex.getMessage());
            }
        }
        return Collections.unmodifiableMap(providers);
    }

    static SmsProviderType resolveProviderType(String providerName) {
        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException("provider name is blank");
        }
        String normalized = providerName.trim();
        try {
            return SmsProviderType.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return SmsProviderType.fromCode(normalized.toLowerCase());
        }
    }
}
