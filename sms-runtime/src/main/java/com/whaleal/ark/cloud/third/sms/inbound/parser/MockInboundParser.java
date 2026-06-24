package com.whaleal.ark.cloud.third.sms.inbound.parser;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.inbound.entity.SmsInboundMessage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Mock测试平台上行短信解析器
 * 用于测试和开发环境的模拟上行短信解析
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class MockInboundParser implements InboundParser {
    
    private final Random random = new Random();
    
    // 模拟关键词列表
    private static final List<String> MOCK_KEYWORDS = Arrays.asList(
            "TD", "退订", "STOP", "UNSUBSCRIBE",
            "DY", "订阅", "START", "SUBSCRIBE",
            "CX", "查询", "QUERY", "CHECK",
            "HELP", "帮助", "?", "？"
    );
    
    // 模拟指令列表
    private static final List<String> MOCK_COMMANDS = Arrays.asList(
            "BALANCE", "余额", "QUERY BALANCE",
            "HISTORY", "历史", "QUERY HISTORY",
            "STATUS", "状态", "CHECK STATUS",
            "RESET", "重置", "RESET PASSWORD"
    );
    
    @Override
    public SmsInboundMessage parse(Map<String, Object> rawData, SmsProviderConfig config) {
        if (!isValidData(rawData)) {
            throw new IllegalArgumentException("无效的Mock上行短信数据");
        }
        
        log.debug("Mock解析上行短信数据: {}", rawData);
        
        String content = getString(rawData, "content", generateMockContent());
        SmsInboundMessage.MessageType messageType = detectMessageType(content);
        
        return SmsInboundMessage.builder()
                .messageId(getString(rawData, "messageId", "mock_inbound_" + System.currentTimeMillis()))
                .providerMessageId("mock_provider_inbound_" + System.currentTimeMillis())
                .from(getString(rawData, "from", generateMockPhoneNumber()))
                .to(getString(rawData, "to", "10690"))
                .content(content)
                .messageType(messageType)
                .encoding(detectEncoding(content))
                .messageCount(calculateMessageCount(content))
                .keywordInfo(createKeywordInfo(content, messageType))
                .commandInfo(createCommandInfo(content, messageType))
                .sentTime(LocalDateTime.now().minusMinutes(random.nextInt(10)))
                .receivedTime(LocalDateTime.now())
                .userInfo(createMockUserInfo())
                .contextInfo(createMockContextInfo())
                .rawData(rawData)
                .build();
    }
    
    /**
     * 检测消息类型
     */
    private SmsInboundMessage.MessageType detectMessageType(String content) {
        if (content == null) {
            return SmsInboundMessage.MessageType.UNKNOWN;
        }
        
        String upperContent = content.toUpperCase();
        
        // 检测关键词
        for (String keyword : MOCK_KEYWORDS) {
            if (upperContent.contains(keyword.toUpperCase())) {
                return SmsInboundMessage.MessageType.KEYWORD;
            }
        }
        
        // 检测指令
        for (String command : MOCK_COMMANDS) {
            if (upperContent.contains(command.toUpperCase())) {
                return SmsInboundMessage.MessageType.COMMAND;
            }
        }
        
        // 检测退订
        if (upperContent.contains("TD") || upperContent.contains("退订") || upperContent.contains("STOP")) {
            return SmsInboundMessage.MessageType.UNSUBSCRIPTION;
        }
        
        // 检测订阅
        if (upperContent.contains("DY") || upperContent.contains("订阅") || upperContent.contains("START")) {
            return SmsInboundMessage.MessageType.SUBSCRIPTION;
        }
        
        return SmsInboundMessage.MessageType.TEXT;
    }
    
    /**
     * 检测编码类型
     */
    private String detectEncoding(String content) {
        if (content == null) return "UTF-8";
        
        // 简单检测：包含中文则是UTF-8，否则是GSM7
        return content.matches(".*[\\u4e00-\\u9fa5].*") ? "UTF-8" : "GSM7";
    }
    
    /**
     * 计算消息条数
     */
    private Integer calculateMessageCount(String content) {
        if (content == null) return 1;
        
        // 简单计算：中文70字符一条，英文160字符一条
        boolean hasChinese = content.matches(".*[\\u4e00-\\u9fa5].*");
        int maxLength = hasChinese ? 70 : 160;
        
        return (content.length() / maxLength) + 1;
    }
    
    /**
     * 创建关键词信息
     */
    private SmsInboundMessage.KeywordInfo createKeywordInfo(String content, SmsInboundMessage.MessageType messageType) {
        if (messageType != SmsInboundMessage.MessageType.KEYWORD) {
            return null;
        }
        
        // 查找匹配的关键词
        String matchedKeyword = null;
        String upperContent = content.toUpperCase();
        
        for (String keyword : MOCK_KEYWORDS) {
            if (upperContent.contains(keyword.toUpperCase())) {
                matchedKeyword = keyword;
                break;
            }
        }
        
        if (matchedKeyword == null) {
            return null;
        }
        
        return SmsInboundMessage.KeywordInfo.builder()
                .keyword(matchedKeyword)
                .keywordType(getKeywordType(matchedKeyword))
                .matchRule("contains")
                .confidence(0.95)
                .possibleKeywords(Arrays.asList(matchedKeyword))
                .build();
    }
    
    /**
     * 获取关键词类型
     */
    private String getKeywordType(String keyword) {
        if (Arrays.asList("TD", "退订", "STOP", "UNSUBSCRIBE").contains(keyword)) {
            return "unsubscribe";
        } else if (Arrays.asList("DY", "订阅", "START", "SUBSCRIBE").contains(keyword)) {
            return "subscribe";
        } else if (Arrays.asList("CX", "查询", "QUERY", "CHECK").contains(keyword)) {
            return "query";
        } else {
            return "help";
        }
    }
    
    /**
     * 创建指令信息
     */
    private SmsInboundMessage.CommandInfo createCommandInfo(String content, SmsInboundMessage.MessageType messageType) {
        if (messageType != SmsInboundMessage.MessageType.COMMAND) {
            return null;
        }
        
        // 查找匹配的指令
        String matchedCommand = null;
        String upperContent = content.toUpperCase();
        
        for (String command : MOCK_COMMANDS) {
            if (upperContent.contains(command.toUpperCase())) {
                matchedCommand = command;
                break;
            }
        }
        
        if (matchedCommand == null) {
            return null;
        }
        
        // 模拟解析参数
        Map<String, String> parameters = new HashMap<>();
        if (matchedCommand.contains("BALANCE")) {
            parameters.put("type", "balance");
            parameters.put("account", "default");
        } else if (matchedCommand.contains("HISTORY")) {
            parameters.put("type", "history");
            parameters.put("days", "30");
        }
        
        return SmsInboundMessage.CommandInfo.builder()
                .command(matchedCommand)
                .parameters(parameters)
                .commandType(getCommandType(matchedCommand))
                .isValid(true)
                .build();
    }
    
    /**
     * 获取指令类型
     */
    private String getCommandType(String command) {
        if (command.contains("BALANCE") || command.contains("余额")) {
            return "balance_query";
        } else if (command.contains("HISTORY") || command.contains("历史")) {
            return "history_query";
        } else if (command.contains("STATUS") || command.contains("状态")) {
            return "status_query";
        } else {
            return "system_command";
        }
    }
    
    /**
     * 创建模拟用户信息
     */
    private SmsInboundMessage.UserInfo createMockUserInfo() {
        String[] regions = {"北京", "上海", "广州", "深圳", "杭州", "成都"};
        String[] carriers = {"中国移动", "中国联通", "中国电信"};
        List<String> tags = Arrays.asList("VIP用户", "活跃用户", "新用户");
        
        return SmsInboundMessage.UserInfo.builder()
                .userId("mock_user_" + random.nextInt(10000))
                .userTags(tags.subList(0, random.nextInt(tags.size()) + 1))
                .userRegion(regions[random.nextInt(regions.length)])
                .carrier(carriers[random.nextInt(carriers.length)])
                .isNewUser(random.nextBoolean())
                .lastActiveTime(LocalDateTime.now().minusDays(random.nextInt(30)))
                .build();
    }
    
    /**
     * 创建模拟上下文信息
     */
    private SmsInboundMessage.ContextInfo createMockContextInfo() {
        String[] scenes = {"用户服务", "营销活动", "系统通知", "客户咨询"};
        String[] statuses = {"待处理", "处理中", "已处理", "已关闭"};
        
        return SmsInboundMessage.ContextInfo.builder()
                .sessionId("mock_session_" + System.currentTimeMillis())
                .conversationTurn(random.nextInt(5) + 1)
                .previousMessageId("mock_prev_" + (System.currentTimeMillis() - 1000))
                .relatedOutboundMessageId("mock_outbound_" + (System.currentTimeMillis() - 2000))
                .businessScene(scenes[random.nextInt(scenes.length)])
                .processStatus(statuses[random.nextInt(statuses.length)])
                .build();
    }
    
    /**
     * 生成模拟内容
     */
    private String generateMockContent() {
        String[] mockContents = {
                "TD", "退订", "STOP",
                "DY", "订阅", "START",
                "查询余额", "QUERY BALANCE",
                "帮助", "HELP", "?",
                "你好，请问有什么可以帮助您的吗？",
                "我想了解一下最新的优惠活动",
                "请发送验证码到我的手机",
                "谢谢您的服务，非常满意！"
        };
        
        return mockContents[random.nextInt(mockContents.length)];
    }
    
    /**
     * 生成模拟手机号
     */
    private String generateMockPhoneNumber() {
        String[] prefixes = {"+86138", "+86139", "+86150", "+86151", "+86188", "+86189"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 4; i++) {
            sb.append("*");
        }
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
    
    /**
     * 获取字符串值，支持默认值
     */
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    @Override
    public String getSupportedProvider() {
        return "MOCK";
    }
} 