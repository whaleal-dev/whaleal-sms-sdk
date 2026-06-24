package com.whaleal.ark.cloud.third.sms.report.fetcher;

import com.whaleal.ark.cloud.third.sms.config.SmsProviderConfig;
import com.whaleal.ark.cloud.third.sms.report.entity.SmsReport;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mock测试平台状态查询器
 * 用于测试和开发环境的模拟状态查询
 * 
 * @author whaleal-dev
 * @since 1.0.0
 */
@Slf4j
public class MockReportFetcher implements ReportFetcher {
    
    private final Random random = new Random();
    
    @Override
    public SmsReport fetchReport(String messageId, SmsProviderConfig config) {
        log.debug("Mock查询状态报告 - 消息ID: {}", messageId);
        
        // 模拟查询延迟
        simulateDelay();
        
        // 模拟不同的状态
        SmsReport.ReportStatus status = simulateReportStatus();
        
        return SmsReport.builder()
                .reportId("mock_report_" + System.currentTimeMillis())
                .messageId(messageId)
                .currentStatus(status)
                .statusCode(status.getCode())
                .statusDescription("Mock模拟状态: " + status.getDescription())
                .statusHistory(createMockStatusHistory(status))
                .submittedTime(LocalDateTime.now().minusMinutes(random.nextInt(60)))
                .lastUpdatedTime(LocalDateTime.now())
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(random.nextInt(10)))
                .retryInfo(createMockRetryInfo())
                .routeInfo(createMockRouteInfo())
                .costInfo(createMockCostInfo())
                .build();
    }
    
    @Override
    public List<SmsReport> fetchReports(List<String> messageIds, SmsProviderConfig config) {
        log.debug("Mock批量查询状态报告 - 数量: {}", messageIds.size());
        
        return messageIds.stream()
                .map(id -> fetchReport(id, config))
                .toList();
    }
    
    @Override
    public List<SmsReport> fetchBatchReports(String batchId, SmsProviderConfig config) {
        log.debug("Mock查询批次状态报告 - 批次ID: {}", batchId);
        
        // 模拟批次包含多个消息
        int messageCount = random.nextInt(5) + 1; // 1-5条消息
        List<SmsReport> reports = new ArrayList<>();
        
        for (int i = 0; i < messageCount; i++) {
            String messageId = batchId + "_msg_" + (i + 1);
            SmsReport report = fetchReport(messageId, config);
            report.setBatchId(batchId);
            reports.add(report);
        }
        
        return reports;
    }
    
    /**
     * 模拟查询延迟
     */
    private void simulateDelay() {
        try {
            Thread.sleep(random.nextInt(200) + 50); // 50-250ms随机延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 模拟状态报告状态
     * 70%已送达，15%已发送，10%失败，5%其他
     */
    private SmsReport.ReportStatus simulateReportStatus() {
        int probability = random.nextInt(100);
        
        if (probability < 70) {
            return SmsReport.ReportStatus.DELIVERED;
        } else if (probability < 85) {
            return SmsReport.ReportStatus.SENT;
        } else if (probability < 95) {
            return SmsReport.ReportStatus.FAILED;
        } else {
            SmsReport.ReportStatus[] otherStatuses = {
                SmsReport.ReportStatus.SUBMITTED,
                SmsReport.ReportStatus.QUEUED,
                SmsReport.ReportStatus.EXPIRED,
                SmsReport.ReportStatus.REJECTED
            };
            return otherStatuses[random.nextInt(otherStatuses.length)];
        }
    }
    
    /**
     * 创建模拟状态历史
     */
    private List<SmsReport.StatusHistory> createMockStatusHistory(SmsReport.ReportStatus finalStatus) {
        List<SmsReport.StatusHistory> history = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(random.nextInt(60));
        
        // 提交状态
        history.add(SmsReport.StatusHistory.builder()
                .status(SmsReport.ReportStatus.SUBMITTED)
                .statusCode("SUBMITTED")
                .statusDescription("消息已提交")
                .statusTime(baseTime)
                .nodeInfo("Mock Gateway")
                .build());
        
        // 接受状态
        history.add(SmsReport.StatusHistory.builder()
                .status(SmsReport.ReportStatus.ACCEPTED)
                .statusCode("ACCEPTED")
                .statusDescription("消息已接受")
                .statusTime(baseTime.plusMinutes(1))
                .nodeInfo("Mock Queue")
                .build());
        
        // 发送状态
        if (finalStatus != SmsReport.ReportStatus.FAILED) {
            history.add(SmsReport.StatusHistory.builder()
                    .status(SmsReport.ReportStatus.SENT)
                    .statusCode("SENT")
                    .statusDescription("消息已发送")
                    .statusTime(baseTime.plusMinutes(2))
                    .nodeInfo("Mock Carrier")
                    .build());
        }
        
        // 最终状态
        if (finalStatus != SmsReport.ReportStatus.SENT) {
            history.add(SmsReport.StatusHistory.builder()
                    .status(finalStatus)
                    .statusCode(finalStatus.getCode())
                    .statusDescription(finalStatus.getDescription())
                    .statusTime(baseTime.plusMinutes(3))
                    .nodeInfo("Mock Device")
                    .build());
        }
        
        return history;
    }
    
    /**
     * 创建模拟重试信息
     */
    private SmsReport.RetryInfo createMockRetryInfo() {
        boolean needRetry = random.nextInt(100) < 20; // 20%概率需要重试
        
        if (!needRetry) {
            return null;
        }
        
        return SmsReport.RetryInfo.builder()
                .retryCount(random.nextInt(3))
                .maxRetries(3)
                .nextRetryTime(LocalDateTime.now().plusMinutes(random.nextInt(30)))
                .retryInterval(300) // 5分钟
                .retryReason("Mock模拟重试")
                .build();
    }
    
    /**
     * 创建模拟路由信息
     */
    private SmsReport.RouteInfo createMockRouteInfo() {
        String[] channels = {"Mock-Primary", "Mock-Secondary", "Mock-Backup"};
        String[] gateways = {"Mock-GW-001", "Mock-GW-002", "Mock-GW-003"};
        String[] carriers = {"Mock-Carrier-A", "Mock-Carrier-B", "Mock-Carrier-C"};
        
        return SmsReport.RouteInfo.builder()
                .routeChannel(channels[random.nextInt(channels.length)])
                .routePriority(random.nextInt(10) + 1)
                .gatewayInfo(gateways[random.nextInt(gateways.length)])
                .carrierInfo(carriers[random.nextInt(carriers.length)])
                .routeTime(LocalDateTime.now().minusMinutes(random.nextInt(30)))
                .build();
    }
    
    /**
     * 创建模拟费用信息
     */
    private SmsReport.CostInfo createMockCostInfo() {
        String[] amounts = {"0.045", "0.050", "0.055", "0.060"};
        String[] currencies = {"CNY", "USD", "EUR"};
        
        return SmsReport.CostInfo.builder()
                .amount(amounts[random.nextInt(amounts.length)])
                .currency(currencies[random.nextInt(currencies.length)])
                .billingType("per_message")
                .messageCount(1)
                .billingTime(LocalDateTime.now().minusMinutes(random.nextInt(10)))
                .build();
    }
    
    @Override
    public String getSupportedProvider() {
        return "MOCK";
    }
} 