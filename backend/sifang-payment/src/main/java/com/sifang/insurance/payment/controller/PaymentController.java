package com.sifang.insurance.payment.controller;

import com.sifang.insurance.common.entity.Result;
import com.sifang.insurance.common.entity.ResultCode;
import com.sifang.insurance.payment.dto.CreatePaymentRequest;
import com.sifang.insurance.payment.dto.PaymentResponse;
import com.sifang.insurance.payment.entity.PaymentRecord;
import com.sifang.insurance.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务控制器
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public Result<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createPayment(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(ResultCode.FAIL.getCode(), e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{paymentNo}")
    public Result<PaymentRecord> queryPaymentStatus(@PathVariable String paymentNo) {
        PaymentRecord record = paymentService.queryPaymentStatus(paymentNo);
        if (record != null) {
            return Result.success(record);
        } else {
            return Result.fail(ResultCode.NOT_FOUND.getCode(), "支付记录不存在");
        }
    }

    /**
     * 根据订单号查询支付信息
     */
    @GetMapping("/order/{orderId}")
    public Result<PaymentRecord> queryPaymentByOrderId(@PathVariable String orderId) {
        PaymentRecord record = paymentService.queryPaymentByOrderId(orderId);
        if (record != null) {
            return Result.success(record);
        } else {
            return Result.fail(ResultCode.NOT_FOUND.getCode(), "支付记录不存在");
        }
    }

    /**
     * 处理支付回调
     */
    @PostMapping("/callback/{paymentMethod}")
    public String processCallback(@PathVariable Integer paymentMethod, 
                               @RequestBody String params, 
                               @RequestParam Map<String, String> queryParams) {
        try {
            log.info("收到支付回调，支付方式: {}", paymentMethod);
            
            // 使用PaymentCallbackHandler处理回调
            boolean result = paymentService.processCallback(paymentMethod, params);
            
            // 返回不同支付渠道要求的回调响应格式
            switch (paymentMethod) {
                case 1: // 支付宝
                    return result ? "success" : "fail";
                case 2: // 微信支付
                    return "<xml><return_code><![CDATA[" + (result ? "SUCCESS" : "FAIL") + "]]></return_code><return_msg><![CDATA[" + (result ? "OK" : "处理失败") + "]]></return_msg></xml>";
                case 3: // 银联支付
                    return result ? "{\"respCode\":\"00\",\"respMsg\":\"成功\"}" : "{\"respCode\":\"99\",\"respMsg\":\"处理失败\"}";
                default:
                    return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            // 根据支付方式返回失败响应
            switch (paymentMethod) {
                case 1: return "fail";
                case 2: return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
                case 3: return "fail";
                default: return "fail";
            }
        }
    }
    
    /**
     * 支付宝回调接口（单独接口）
     */
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> requestParams) {
        log.info("收到支付宝回调请求");
        // 处理支付宝回调
        return "success";
    }
    
    /**
     * 微信支付回调接口（单独接口）
     */
    @PostMapping("/callback/wechat")
    public String wechatCallback() {
        log.info("收到微信支付回调请求");
        // 处理微信支付回调
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /**
     * 发起退款
     */
    @PostMapping("/refund")
    public Result<Boolean> refund(@RequestParam String paymentNo, 
                           @RequestParam BigDecimal amount, 
                           @RequestParam String reason) {
        try {
            log.info("发起退款请求: paymentNo={}, amount={}, reason={}", paymentNo, amount, reason);
            boolean result = paymentService.refund(paymentNo, amount, reason);
            log.info("退款处理结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("退款失败", e);
            return Result.fail(ResultCode.BUSINESS_ERROR.getCode(), "退款失败: " + e.getMessage());
        }
    }
    
    /**
     * 支付测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        log.info("支付服务健康检查");
        return Result.success("支付服务正常运行");
    }
}