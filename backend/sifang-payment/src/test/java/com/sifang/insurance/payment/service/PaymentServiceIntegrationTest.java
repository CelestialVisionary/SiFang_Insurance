package com.sifang.insurance.payment.service;

import com.sifang.insurance.payment.dto.CreatePaymentRequest;
import com.sifang.insurance.payment.dto.PaymentResponse;
import com.sifang.insurance.payment.entity.PaymentRecord;
import com.sifang.insurance.payment.handler.PaymentCallbackHandler;
import com.sifang.insurance.payment.mapper.PaymentRecordMapper;
import com.sifang.insurance.payment.sender.PaymentMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 支付服务集成测试
 */
@ExtendWith(MockitoExtension.class)
public class PaymentServiceIntegrationTest {

    @Mock
    private PaymentRecordMapper paymentRecordMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private PaymentMessageSender paymentMessageSender;

    @Mock
    private PaymentCallbackHandler paymentCallbackHandler;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private CreatePaymentRequest paymentRequest;
    private PaymentRecord paymentRecord;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        paymentRequest = new CreatePaymentRequest();
        paymentRequest.setOrderId("ORDER001");
        paymentRequest.setUserId("USER001");
        paymentRequest.setAmount(new BigDecimal(100));
        paymentRequest.setPaymentMethod(1); // 支付宝
        paymentRequest.setProductName("测试保险产品");

        paymentRecord = new PaymentRecord();
        paymentRecord.setId(1L);
        paymentRecord.setPaymentNo("PAY202312011200001");
        paymentRecord.setOrderId("ORDER001");
        paymentRecord.setUserId("USER001");
        paymentRecord.setAmount(new BigDecimal(100));
        paymentRecord.setPaymentMethod(1);
        paymentRecord.setStatus(0); // 待支付
        paymentRecord.setCreateTime(new Date());
        paymentRecord.setUpdateTime(new Date());
    }

    @Test
    public void testCreatePaymentSuccess() {
        // 模拟查询订单不存在
        when(paymentRecordMapper.selectByOrderId(anyString())).thenReturn(null);
        when(paymentRecordMapper.insert(any(PaymentRecord.class))).thenReturn(1);

        // 执行测试
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getPaymentNo());
        assertEquals("ORDER001", response.getOrderId());
        assertEquals(new BigDecimal(100), response.getAmount());
        assertEquals(0, response.getStatus());
        assertEquals("待支付", response.getStatusDesc());
        assertEquals("支付宝", response.getPaymentMethodName());
        assertNotNull(response.getPaymentUrl());
        assertNotNull(response.getExpireTime());

        // 验证方法调用
        verify(paymentRecordMapper).selectByOrderId("ORDER001");
        verify(paymentRecordMapper).insert(any(PaymentRecord.class));
        verify(redisTemplate).opsForValue().set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    public void testCreatePaymentOrderAlreadyPaid() {
        // 模拟订单已支付
        PaymentRecord paidRecord = new PaymentRecord();
        paidRecord.setStatus(1); // 已支付
        when(paymentRecordMapper.selectByOrderId("ORDER001")).thenReturn(paidRecord);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.createPayment(paymentRequest);
        });

        assertEquals("该订单已支付成功", exception.getMessage());
    }

    @Test
    public void testProcessCallbackSuccess() {
        // 准备回调参数
        String callbackParams = "payment_no=PAY202312011200001&status=success&third_party_payment_no=THIRD123456";
        
        // 模拟签名验证通过
        when(paymentCallbackHandler.verifySignature(anyInt(), anyMap())).thenReturn(true);
        
        // 模拟查询支付记录
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(paymentRecord);
        
        // 模拟更新成功
        when(paymentRecordMapper.updateById(any(PaymentRecord.class))).thenReturn(1);
        
        // 模拟消息发送成功
        when(paymentMessageSender.sendPaymentSuccessMessage(any(PaymentRecord.class))).thenReturn(true);

        // 执行测试
        boolean result = paymentService.processCallback(1, callbackParams);

        // 验证结果
        assertTrue(result);
        assertEquals(1, paymentRecord.getStatus()); // 支付成功
        assertNotNull(paymentRecord.getPaymentTime());
        assertEquals("THIRD123456", paymentRecord.getThirdPartyPaymentNo());

        // 验证方法调用
        verify(paymentCallbackHandler).verifySignature(1, anyMap());
        verify(paymentRecordMapper).selectByPaymentNo("PAY202312011200001");
        verify(paymentRecordMapper).updateById(paymentRecord);
        verify(paymentMessageSender).sendPaymentSuccessMessage(paymentRecord);
    }

    @Test
    public void testProcessCallbackSignatureInvalid() {
        // 准备回调参数
        String callbackParams = "payment_no=PAY202312011200001&status=success";
        
        // 模拟签名验证失败
        when(paymentCallbackHandler.verifySignature(anyInt(), anyMap())).thenReturn(false);

        // 执行测试
        boolean result = paymentService.processCallback(1, callbackParams);

        // 验证结果
        assertFalse(result);
        verify(paymentRecordMapper, never()).selectByPaymentNo(anyString());
    }

    @Test
    public void testProcessCallbackRecordNotFound() {
        // 准备回调参数
        String callbackParams = "payment_no=PAY202312011200001&status=success";
        
        // 模拟签名验证通过
        when(paymentCallbackHandler.verifySignature(anyInt(), anyMap())).thenReturn(true);
        
        // 模拟查询支付记录不存在
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(null);

        // 执行测试
        boolean result = paymentService.processCallback(1, callbackParams);

        // 验证结果
        assertFalse(result);
    }

    @Test
    public void testProcessCallbackAlreadyProcessed() {
        // 准备回调参数
        String callbackParams = "payment_no=PAY202312011200001&status=success";
        
        // 模拟签名验证通过
        when(paymentCallbackHandler.verifySignature(anyInt(), anyMap())).thenReturn(true);
        
        // 模拟支付记录已处理
        paymentRecord.setStatus(1); // 已支付
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(paymentRecord);

        // 执行测试
        boolean result = paymentService.processCallback(1, callbackParams);

        // 验证结果
        assertTrue(result);
        verify(paymentRecordMapper, never()).updateById(any(PaymentRecord.class));
    }

    @Test
    public void testRefundSuccess() {
        // 模拟查询支付记录
        paymentRecord.setStatus(1); // 已支付
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(paymentRecord);
        
        // 模拟更新成功
        when(paymentRecordMapper.updateById(any(PaymentRecord.class))).thenReturn(1);
        
        // 模拟消息发送成功
        when(paymentMessageSender.sendRefundSuccessMessage(any(PaymentRecord.class))).thenReturn(true);

        // 执行测试
        boolean result = paymentService.refund("PAY202312011200001", new BigDecimal(50), "用户申请退款");

        // 验证结果
        assertTrue(result);
        assertEquals(4, paymentRecord.getStatus()); // 已退款

        // 验证方法调用
        verify(paymentRecordMapper).selectByPaymentNo("PAY202312011200001");
        verify(paymentRecordMapper, times(2)).updateById(paymentRecord);
        verify(paymentMessageSender).sendRefundSuccessMessage(paymentRecord);
    }

    @Test
    public void testRefundAmountExceedsPayment() {
        // 模拟查询支付记录
        paymentRecord.setStatus(1); // 已支付
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(paymentRecord);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.refund("PAY202312011200001", new BigDecimal(200), "用户申请退款");
        });

        assertEquals("退款金额不能大于支付金额", exception.getMessage());
    }

    @Test
    public void testRefundOrderNotPaid() {
        // 模拟查询支付记录
        paymentRecord.setStatus(0); // 待支付
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(paymentRecord);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.refund("PAY202312011200001", new BigDecimal(50), "用户申请退款");
        });

        assertEquals("只有支付成功的订单才能退款", exception.getMessage());
    }

    @Test
    public void testQueryPaymentStatus() {
        // 模拟查询支付记录
        when(paymentRecordMapper.selectByPaymentNo("PAY202312011200001")).thenReturn(paymentRecord);

        // 执行测试
        PaymentRecord result = paymentService.queryPaymentStatus("PAY202312011200001");

        // 验证结果
        assertNotNull(result);
        assertEquals("PAY202312011200001", result.getPaymentNo());
        verify(paymentRecordMapper).selectByPaymentNo("PAY202312011200001");
    }

    @Test
    public void testQueryPaymentByOrderId() {
        // 模拟查询支付记录
        when(paymentRecordMapper.selectByOrderId("ORDER001")).thenReturn(paymentRecord);

        // 执行测试
        PaymentRecord result = paymentService.queryPaymentByOrderId("ORDER001");

        // 验证结果
        assertNotNull(result);
        assertEquals("ORDER001", result.getOrderId());
        verify(paymentRecordMapper).selectByOrderId("ORDER001");
    }
}