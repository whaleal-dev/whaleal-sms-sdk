# Whaleal SMS SDK

多供应商短信 SDK，支持 17 个厂商，统一发信、回执解析、上行回信与状态查询。

**组织：** [whaleal-dev](https://github.com/whaleal-dev) · **仓库：** [whaleal-sms-sdk](https://github.com/whaleal-dev/whaleal-sms-sdk) · **官网：** [whaleal.com](https://whaleal.com)

**Maven 坐标：** `com.whaleal.third:whaleal-sms-sdk:1.0.0`

> SDK **不读取** `application.yml`。供应商、秘钥、发送方、回调地址等均在**调用时动态传入**。

## 核心 API

| 类型 | 类 | 作用 |
|------|-----|------|
| 发信门面 | `SmsClient` | 业务代码注入/持有的接口：`send()`、`sendBatch()`、`sendAsync()` |
| 发信工厂 | `SmsClients` | 纯 Java 创建实例：`SmsClients.builder().build()` |
| Webhook 门面 | `SmsWebhookHandler` | 解析回执/上行、主动查状态、号码校验 |
| 发信入参 | `SmsSendRequest` | 手机号、内容/模板、凭证、供应商、callbackUrl |
| 发信出参 | `SmsSendResult` | success、messageId、providerMessageId、errorCode |
| 凭证 | `SmsCredentials` | apiKey/apiSecret 或 accessKeyId/accessKeySecret |

```java
// 创建（纯 Java）
SmsClient client = SmsClients.builder()
        .provider(SmsProviderType.TWILIO)
        .defaultFrom("+1234567890")
        .build();

// 使用
client.send(SmsSendRequest.builder().to(phone).content("hi").build());
```

`SmsClient` 是接口，放在 `sms-api`；`SmsClients` 是 runtime 工厂，命名类似 `Executors`、`Files`。

## 模块结构

```
whaleal-sms-sdk/                  # parent pom（artifactId: whaleal-sms-sdk）
├── sms-api                       # 接口、DTO、枚举、异常（零 Spring）
├── sms-runtime                   # 适配器、MOCK、SPI、DefaultSmsClient
├── sms-providers                 # 17 厂商 HTTP 实现（Java SPI）
├── sms-spring-boot-starter       # Spring Boot 自动配置
└── sms-all                       # 聚合依赖，开箱即用（推荐）
```

| 模块 | Maven 坐标 | 适用场景 |
|------|------------|----------|
| `sms-all` | `com.whaleal.third:sms-all` | **大多数业务**，一条依赖搞定 |
| `sms-api` | `com.whaleal.third:sms-api` | 纯契约层，无 Spring |
| `sms-runtime` | `com.whaleal.third:sms-runtime` | 纯 Java 发信（搭配 api） |
| `sms-providers` | `com.whaleal.third:sms-providers` | 真实厂商 SPI |
| `sms-spring-boot-starter` | `com.whaleal.third:sms-spring-boot-starter` | Spring Boot 自动配置 |

厂商实现通过 `META-INF/services` 注册，**无需修改 runtime 代码**即可扩展新厂商。

## 支持的供应商

| 枚举 | 说明 |
|------|------|
| `TWILIO` | Twilio |
| `VONAGE` | Vonage |
| `MESSAGEBIRD` | MessageBird |
| `PLIVO` | Plivo |
| `INFOBIP` | Infobip |
| `ALIYUN` / `ALIYUN_INTERNATIONAL` | 阿里云（国内/国际） |
| `TENCENT` / `TENCENT_INTERNATIONAL` | 腾讯云（国内/国际） |
| `HUAWEI` / `HUAWEI_INTERNATIONAL` | 华为云（国内/国际） |
| `AWS` | Amazon SNS |
| `CHINA_MOBILE` / `CHINA_TELECOM` / `CHINA_UNICOM` | 三大运营商 |
| `CUSTOM_HTTP` | 自定义 HTTP |
| `MOCK` | 本地测试（无需凭证） |

## 快速开始

### 1. 添加依赖

**推荐（开箱即用）：**

```xml
<dependency>
    <groupId>com.whaleal.third</groupId>
    <artifactId>sms-all</artifactId>
    <version>1.0.0</version>
</dependency>
```

**按需组合：**

```xml
<!-- 纯 Java 发信（MOCK） -->
<dependency>
    <groupId>com.whaleal.third</groupId>
    <artifactId>sms-api</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.whaleal.third</groupId>
    <artifactId>sms-runtime</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 真实厂商（SPI） -->
<dependency>
    <groupId>com.whaleal.third</groupId>
    <artifactId>sms-providers</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Spring Boot 自动配置 -->
<dependency>
    <groupId>com.whaleal.third</groupId>
    <artifactId>sms-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Spring Boot

不自定义 `@Bean` 时，自动配置提供默认 `SmsClient` 与 `SmsWebhookHandler`（MOCK 供应商，用于本地调试）。

```java
@Configuration
public class SmsConfig {

    @Bean
    public SmsClient smsClient() {
        return SmsClients.builder()
                .provider(SmsProviderType.TWILIO)
                .defaultFrom("+1234567890")
                .signName("YourBrand")
                .deliveryReceiptUrl("https://api.example.com/sms/webhook/receipt")
                .build();
    }
}
```

### 3. 发送短信

```java
@Service
@RequiredArgsConstructor
public class NotifyService {
    private final SmsClient smsClient;
    private final TenantSmsCredentialService credentialService;

    public void sendCode(String tenantId, String phone, String code) {
        SmsSendResult result = smsClient.send(SmsSendRequest.builder()
                .provider(SmsProviderType.TWILIO)
                .to(phone)
                .content("您的验证码是 " + code)
                .callbackUrl("https://api.example.com/sms/webhook/receipt")
                .credentials(credentialService.resolve(tenantId))
                .build());

        if (!result.isSuccess()) {
            throw new IllegalStateException(result.getErrorMessage());
        }
    }
}
```

**模板短信：**

```java
smsClient.send(SmsSendRequest.builder()
        .provider(SmsProviderType.ALIYUN)
        .to(phone)
        .templateId("SMS_123456")
        .templateParams(Map.of("code", code))
        .credentials(credentials)
        .build());
```

**批量 / 异步：**

```java
List<SmsSendResult> results = smsClient.sendBatch(List.of(req1, req2));
CompletableFuture<SmsSendResult> future = smsClient.sendAsync(req);
```

每条 `SmsSendRequest` 可独立指定 `provider` 与 `credentials`，便于多租户、多通道切换。

### SmsCredentials

```java
// Twilio / Vonage 等
SmsCredentials.builder()
        .apiKey("account-sid")
        .apiSecret("auth-token")
        .build();

// 阿里云 / 腾讯云 / 华为云
SmsCredentials.builder()
        .accessKeyId("LTAI...")
        .accessKeySecret("secret...")
        .build();
```

## 纯 Java（无 Spring）

```java
SmsClient client = SmsClients.builder()
        .provider(SmsProviderType.TWILIO)
        .defaultFrom("+1234567890")
        .build();

client.send(SmsSendRequest.builder()
        .provider(SmsProviderType.TWILIO)
        .to("+8613800138000")
        .content("Hello")
        .credentials(SmsCredentials.builder()
                .apiKey("sid")
                .apiSecret("token")
                .build())
        .build());
```

Webhook 解析在 Spring 外需自行构造 `DefaultSmsWebhookHandler`（或通过 starter 注入）。

## 送达回执（Callback）

SDK **不提供 HTTP 服务**，只负责两件事：

1. **发信时**把 callback URL 传给支持 per-message callback 的厂商
2. **收到 POST 后**用 `SmsWebhookHandler` 解析 payload

```
你的服务                         厂商（如 Twilio）
   │                                   │
   │── send(..., callbackUrl=你的URL) ──►│
   │                                   │
   │◄── 送达/失败后 POST 回执 ─────────────│
   │    webhookHandler.parseReceipt()  │
```

### 配置 callback URL

```java
// 方式 1：单次请求指定（优先级最高）
smsClient.send(SmsSendRequest.builder()
        .to(phone)
        .content("hello")
        .callbackUrl("https://api.example.com/sms/webhook/receipt")
        .build());

// 方式 2：构建 Client 时设置全局默认
SmsClient client = SmsClients.builder()
        .provider(SmsProviderType.TWILIO)
        .deliveryReceiptUrl("https://api.example.com/sms/webhook/receipt")
        .callbackUrl("https://api.example.com/sms/webhook/fallback")  // 备选
        .build();
```

**优先级：** `request.callbackUrl` > `deliveryReceiptUrl` > `callbackUrl`

### 厂商差异

| 类型 | 发信时传 callback | 说明 |
|------|-------------------|------|
| Twilio、Vonage、Plivo、MessageBird、Infobip | ✅ | SDK 自动写入厂商 API 参数 |
| 阿里云、腾讯云、华为云（国内） | ❌ | 在厂商控制台配置回执 URL |
| MOCK | — | 忽略 callback，用于本地调试 |

## Webhook 解析

Spring 项目注入 `SmsWebhookHandler`；收到厂商 POST 后在 Controller 中解析。

### 送达回执（Delivery Receipt）

短信是否成功送达用户手机。

```java
@RestController
@RequestMapping("/sms/webhook")
@RequiredArgsConstructor
public class SmsWebhookController {
    private final SmsWebhookHandler webhookHandler;

    @PostMapping("/twilio/receipt")
    public ResponseEntity<Void> twilioReceipt(@RequestParam Map<String, Object> payload) {
        SmsReceipt receipt = webhookHandler.parseReceipt(SmsProviderType.TWILIO, payload);
        // receipt.getReceiptStatus() / getMessageId() / getTo()
        return ResponseEntity.ok().build();
    }
}
```

也可省略厂商参数，由 SDK 自动识别：`webhookHandler.parseReceipt(payload)`。

### 上行回信（Inbound）

用户回复短信时，厂商 POST 到你配置的 Inbound URL（通常在厂商控制台配置，与发信 callback 不同）。

```java
@PostMapping("/twilio/inbound")
public ResponseEntity<Void> twilioInbound(@RequestParam Map<String, Object> payload) {
    SmsInboundMessage inbound = webhookHandler.parseInbound(SmsProviderType.TWILIO, payload);
    // inbound.getFrom() / getContent()
    return ResponseEntity.ok().build();
}
```

### 主动查状态（Report）

不等 callback，主动调厂商 API 查询发送链路状态。

```java
SmsReport report = webhookHandler.fetchReport(
        SmsProviderType.TWILIO, messageId, credentials);

List<SmsReport> reports = webhookHandler.fetchReports(
        SmsProviderType.TWILIO, messageIds, credentials);
```

### 号码校验

```java
PhoneValidationResult result = webhookHandler.validatePhone(
        SmsProviderType.TWILIO, "+8613800138000", credentials);
```

## 错误码

| 代码 | 说明 |
|------|------|
| E001 | 请求参数不合法（手机号/内容/模板缺失等） |
| E002 | 缺少发送凭证（非 MOCK 须传 `credentials`） |
| PROVIDER_ERROR | 厂商返回业务错误 |
| SEND_ERROR | 发送过程异常 |

## 设计说明

- **零配置启动**：不依赖 yaml，秘钥不落盘，适合 SaaS 多租户
- **按请求选通道**：每条 `SmsSendRequest` 可指定不同 `provider` + `credentials`
- **SPI 扩展**：新增厂商只需在 `sms-providers` 实现并注册 `META-INF/services`
- **模块分离**：`sms-api` 零 Spring，可被非 Spring 项目直接依赖

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.4.13 |
| Hutool | 5.8.43 |
| FastJSON2 | 2.0.59 |

## 构建

```bash
mvn clean test package
```

## 许可证

Copyright © 2026 [whaleal-dev](https://github.com/whaleal-dev) · [whaleal.com](https://whaleal.com)

维护者：hbnking

本项目基于 [Apache License 2.0](LICENSE) 发布。
