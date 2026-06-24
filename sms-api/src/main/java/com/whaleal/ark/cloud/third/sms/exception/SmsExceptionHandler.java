package com.whaleal.ark.cloud.third.sms.exception;

import java.util.function.Consumer;

/**
 * SMS 异常处理工具类
 * 提供统一的异常处理方法，帮助业务层根据异常类型进行不同的处理
 * 
 */
public class SmsExceptionHandler {
    
    /**
     * 处理SMS异常
     * 根据异常类型执行不同的处理逻辑
     */
    public static void handle(SmsException exception, SmsExceptionHandlers handlers) {
        if (exception instanceof SmsTimeoutException) {
            if (handlers.timeoutHandler != null) {
                handlers.timeoutHandler.accept((SmsTimeoutException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else if (exception instanceof SmsCredentialsException) {
            if (handlers.credentialsHandler != null) {
                handlers.credentialsHandler.accept((SmsCredentialsException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else if (exception instanceof SmsParameterException) {
            if (handlers.parameterHandler != null) {
                handlers.parameterHandler.accept((SmsParameterException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else if (exception instanceof SmsNetworkException) {
            if (handlers.networkHandler != null) {
                handlers.networkHandler.accept((SmsNetworkException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else if (exception instanceof SmsQuotaException) {
            if (handlers.quotaHandler != null) {
                handlers.quotaHandler.accept((SmsQuotaException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else if (exception instanceof SmsConfigException) {
            if (handlers.configHandler != null) {
                handlers.configHandler.accept((SmsConfigException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else if (exception instanceof SmsContentException) {
            if (handlers.contentHandler != null) {
                handlers.contentHandler.accept((SmsContentException) exception);
            } else if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        } else {
            // 其他未知的SmsException子类
            if (handlers.defaultHandler != null) {
                handlers.defaultHandler.accept(exception);
            }
        }
    }
    
    /**
     * 判断异常是否可重试
     */
    public static boolean isRetryable(SmsException exception) {
        if (exception instanceof SmsTimeoutException) {
            return true; // 超时可重试
        } else if (exception instanceof SmsNetworkException) {
            return true; // 网络错误可重试
        } else if (exception instanceof SmsQuotaException) {
            SmsQuotaException quotaEx = (SmsQuotaException) exception;
            return quotaEx.getRetryAfterSeconds() != null; // 有重试时间的可重试
        } else if (exception instanceof SmsCredentialsException) {
            return false; // 认证错误不可重试
        } else if (exception instanceof SmsParameterException) {
            return false; // 参数错误不可重试
        } else if (exception instanceof SmsConfigException) {
            return false; // 配置错误不可重试
        } else if (exception instanceof SmsContentException) {
            return false; // 内容违规不可重试
        }
        return false; // 默认不可重试
    }
    
    /**
     * 获取建议的重试延迟时间（秒）
     */
    public static int getRetryDelaySeconds(SmsException exception) {
        if (exception instanceof SmsQuotaException) {
            SmsQuotaException quotaEx = (SmsQuotaException) exception;
            if (quotaEx.getRetryAfterSeconds() != null) {
                return quotaEx.getRetryAfterSeconds();
            }
        } else if (exception instanceof SmsTimeoutException) {
            return 30; // 超时默认等待30秒
        } else if (exception instanceof SmsNetworkException) {
            return 60; // 网络错误默认等待60秒
        }
        return 0; // 不可重试或无建议延迟
    }
    
    /**
     * 异常处理器集合
     */
    public static class SmsExceptionHandlers {
        public Consumer<SmsTimeoutException> timeoutHandler;
        public Consumer<SmsCredentialsException> credentialsHandler;
        public Consumer<SmsParameterException> parameterHandler;
        public Consumer<SmsNetworkException> networkHandler;
        public Consumer<SmsQuotaException> quotaHandler;
        public Consumer<SmsConfigException> configHandler;
        public Consumer<SmsContentException> contentHandler;
        public Consumer<SmsException> defaultHandler;
        
        public SmsExceptionHandlers onTimeout(Consumer<SmsTimeoutException> handler) {
            this.timeoutHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onCredentials(Consumer<SmsCredentialsException> handler) {
            this.credentialsHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onParameter(Consumer<SmsParameterException> handler) {
            this.parameterHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onNetwork(Consumer<SmsNetworkException> handler) {
            this.networkHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onQuota(Consumer<SmsQuotaException> handler) {
            this.quotaHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onConfig(Consumer<SmsConfigException> handler) {
            this.configHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onContent(Consumer<SmsContentException> handler) {
            this.contentHandler = handler;
            return this;
        }
        
        public SmsExceptionHandlers onDefault(Consumer<SmsException> handler) {
            this.defaultHandler = handler;
            return this;
        }
    }
    
    /**
     * 创建异常处理器构建器
     */
    public static SmsExceptionHandlers handlers() {
        return new SmsExceptionHandlers();
    }
} 