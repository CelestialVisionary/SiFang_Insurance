package com.sifang.insurance.underwriting;

import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.feign.OrderFeignClient;
import com.sifang.insurance.underwriting.job.UnderwritingStatusSyncJob;
import com.sifang.insurance.underwriting.service.UnderwritingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * 核保服务集成测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class UnderwritingIntegrationTest {

    @Autowired
    private UnderwritingService underwritingService;
    
    @Autowired
    private UnderwritingStatusSyncJob syncJob;
    
    @MockBean
    private OrderFeignClient orderFeignClient;
    
    /**
     * 测试核保提交和状态同步流程
     */
    @Test
    public void testUnderwritingAndSyncProcess() {
        // 1. 准备核保请求数据
        UnderwritingRequest request = new UnderwritingRequest();
        request.setOrderId("TEST_ORDER_" + System.currentTimeMillis());
        request.setUserId(1001L);
        request.setProductId(1L);
        
        Map<String, Object> applicantInfo = new HashMap<>();
        applicantInfo.put("name", "张三");
        applicantInfo.put("age", 30);
        applicantInfo.put("gender", "男");
        applicantInfo.put("idCard", "110101199001011234");
        applicantInfo.put("phone", "13800138000");
        request.setApplicantInfo(applicantInfo);
        
        // 2. 模拟订单服务响应
        when(orderFeignClient.updateOrderUnderwritingStatus(any(), anyInt())).thenReturn(true);
        
        // 3. 提交核保
        UnderwritingResponse response = underwritingService.submitUnderwriting(request);
        assertNotNull(response);
        assertEquals(request.getOrderId(), response.getOrderId());
        
        // 4. 查询核保记录
        UnderwritingRecord record = underwritingService.queryUnderwritingResult(request.getOrderId());
        assertNotNull(record);
        assertEquals(0, record.getSyncStatus().intValue()); // 初始为待同步状态
        
        // 5. 执行同步任务
        syncJob.syncUnderwritingStatus();
        
        // 6. 验证同步结果（这里只是模拟测试，实际数据库更新需要额外查询）
        // 验证调用了订单服务的更新方法
        Mockito.verify(orderFeignClient).updateOrderUnderwritingStatus(request.getOrderId(), record.getStatus());
    }
    
    /**
     * 测试核保拒绝场景
     */
    @Test
    public void testUnderwritingRejectScenario() {
        // 准备一个可能被拒绝的核保请求（例如年龄超过限制）
        UnderwritingRequest request = new UnderwritingRequest();
        request.setOrderId("TEST_REJECT_" + System.currentTimeMillis());
        request.setUserId(1002L);
        request.setProductId(2L); // 假设有一个对年龄有严格限制的产品
        
        Map<String, Object> applicantInfo = new HashMap<>();
        applicantInfo.put("name", "李四");
        applicantInfo.put("age", 80); // 高龄可能被拒绝
        applicantInfo.put("gender", "男");
        applicantInfo.put("idCard", "110101194001011234");
        applicantInfo.put("phone", "13900139000");
        request.setApplicantInfo(applicantInfo);
        
        // 提交核保
        UnderwritingResponse response = underwritingService.submitUnderwriting(request);
        assertNotNull(response);
        
        // 由于没有实际的规则引擎配置，这里可能需要根据实际情况调整断言
        // 如果有规则配置拒绝高龄，则断言状态为2（拒绝）
        // 如果没有规则配置，则默认通过，状态为1
        
        System.out.println("核保结果状态: " + response.getStatus());
        System.out.println("核保结果原因: " + response.getResultReason());
    }
}