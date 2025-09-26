package com.sifang.insurance.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sifang.insurance.common.entity.ResponseResult;
import com.sifang.insurance.order.entity.Order;
import com.sifang.insurance.order.feign.UnderwritingFeignClient;
import com.sifang.insurance.order.service.OrderService;
import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单与核保服务集成测试
 */
@SpringBootTest
public class OrderUnderwritingIntegrationTest {

    @Mock
    private UnderwritingFeignClient underwritingFeignClient;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderServiceImpl orderServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderAndSubmitUnderwriting() {
        // 准备测试数据
        Order order = new Order();
        order.setUserId(1L);
        order.setProductId(1001L);
        order.setProductName("测试产品");
        order.setPremiumAmount(100.0);
        order.setInsuredAmount(10000.0);

        // 模拟核保服务响应
        UnderwritingResponse underwritingResponse = new UnderwritingResponse();
        underwritingResponse.setOrderId("ORDER20240101000001");
        underwritingResponse.setStatus(1); // 核保通过
        ResponseResult<UnderwritingResponse> result = new ResponseResult<>(200, "成功", underwritingResponse);

        when(underwritingFeignClient.submitUnderwriting(any(UnderwritingRequest.class))).thenReturn(result);
        when(orderServiceMock.save(order)).thenReturn(true);

        // 执行测试
        Order createdOrder = orderServiceMock.createOrder(order);

        // 验证结果
        verify(underwritingFeignClient).submitUnderwriting(any(UnderwritingRequest.class));
        assertEquals(0, createdOrder.getUnderwritingStatus()); // 初始状态为待核保
        // 实际业务中，submitUnderwritingRequest方法会异步调用，这里通过模拟验证调用了核保服务
    }

    @Test
    void testUnderwritingStatusSync() {
        // 准备测试数据
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORDER20240101000001");
        order.setUnderwritingStatus(0); // 待核保

        // 模拟订单查询
        when(orderServiceMock.getOrdersByUnderwritingStatus(List.of(0, 3))).thenReturn(List.of(order));

        // 模拟核保服务查询响应
        UnderwritingRecord underwritingRecord = new UnderwritingRecord();
        underwritingRecord.setStatus(1); // 核保通过
        ResponseResult<UnderwritingRecord> result = new ResponseResult<>(200, "成功", underwritingRecord);
        when(underwritingFeignClient.queryUnderwritingResult(order.getOrderNo())).thenReturn(result);

        // 模拟更新操作
        when(orderServiceMock.updateUnderwritingStatus(order.getId(), 1)).thenReturn(true);

        // 创建定时任务实例并执行同步方法
        UnderwritingStatusSyncJob syncJob = new UnderwritingStatusSyncJob();
        syncJob.orderService = orderServiceMock;
        syncJob.underwritingFeignClient = underwritingFeignClient;

        // 执行同步
        syncJob.syncUnderwritingStatus();

        // 验证结果
        verify(orderServiceMock).getOrdersByUnderwritingStatus(List.of(0, 3));
        verify(underwritingFeignClient).queryUnderwritingResult(order.getOrderNo());
        verify(orderServiceMock).updateUnderwritingStatus(order.getId(), 1);
    }

    @Test
    void testUnderwritingRejection() {
        // 准备测试数据
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORDER20240101000002");
        order.setUnderwritingStatus(0); // 待核保

        // 模拟订单查询
        when(orderServiceMock.getOrdersByUnderwritingStatus(List.of(0, 3))).thenReturn(List.of(order));

        // 模拟核保拒绝响应
        UnderwritingRecord underwritingRecord = new UnderwritingRecord();
        underwritingRecord.setStatus(2); // 核保拒绝
        ResponseResult<UnderwritingRecord> result = new ResponseResult<>(200, "成功", underwritingRecord);
        when(underwritingFeignClient.queryUnderwritingResult(order.getOrderNo())).thenReturn(result);

        // 模拟更新操作
        when(orderServiceMock.updateUnderwritingStatus(order.getId(), 2)).thenReturn(true);
        when(orderServiceMock.updateOrderStatus(order.getId(), 2)).thenReturn(true); // 2表示已取消

        // 创建定时任务实例并执行同步方法
        UnderwritingStatusSyncJob syncJob = new UnderwritingStatusSyncJob();
        syncJob.orderService = orderServiceMock;
        syncJob.underwritingFeignClient = underwritingFeignClient;

        // 执行同步
        syncJob.syncUnderwritingStatus();

        // 验证结果
        verify(orderServiceMock).updateUnderwritingStatus(order.getId(), 2);
        verify(orderServiceMock).updateOrderStatus(order.getId(), 2); // 验证订单被取消
    }

    // 模拟OrderServiceImpl类，用于测试
    static class OrderServiceImpl extends com.sifang.insurance.order.service.impl.OrderServiceImpl {
        // 重写save方法用于测试
        @Override
        public boolean save(Order entity) {
            return super.save(entity);
        }
    }
}