package com.whaleal.ark.cloud.third.sms.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 渠道详细信息实体
 * 用于redis 缓存
 *
 */
@Data
public class SmsChannelDetails implements Serializable {

    private Long  id ;
    private String platformType;
    private String description;
    private String apiUrl;
    private String apiKey;
    private String apiSecret;

}