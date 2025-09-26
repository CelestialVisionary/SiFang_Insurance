package com.sifang.insurance.payment.controller;

import com.sifang.insurance.common.api.R;
import com.sifang.insurance.payment.dto.CreatePaymentRequest;
import com.sifang.insurance.payment.dto.PaymentResponse;
import com.sifang.insurance.payment.entity.PaymentRecord;
import com.sifang.insurance.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付服务控制器
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public R<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createPayment(request);
            return R.ok(response);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{paymentNo}")
    public R<PaymentRecord> queryPaymentStatus(@PathVariable String paymentNo) {
        PaymentRecord record = paymentService.queryPaymentStatus(paymentNo);
        if (record != null) {
            return R.ok(record);
        } else {
            return R.error("支付记录不存在");
        }
    }

    /**
     * 根据订单号查询支付信息
     */
    @GetMapping("/order/{orderId}")
    public R<PaymentRecord> queryPaymentByOrderId(@PathVariable String orderId) {
        PaymentRecord record = paymentService.queryPaymentByOrderId(orderId);
        if (record != null) {
            return R.ok(record);
        } else {
            return R.error("支付记录不存在");
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
            // 根据不同支付方式处理回调参数
            String callbackParams;
            if (params != null && !params.isEmpty()) {
                callbackParams = params;
            } else {
                // 构建查询参数字符串
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                callbackParams = sb.toString();
            }
            
            boolean result = paymentService.processCallback(paymentMethod, callbackParams);
            
            // 返回不同支付渠道要求的回调响应格式
            switch (paymentMethod) {
                case 1: // 支付宝
                    return result ? "success" : "fail";
                case 2: // 微信支付
                    return "<xml><return_code><![CDATA[" + (result ? "SUCCESS" : "FAIL") + "]]></return_code></xml>";
                case 3: // 银联支付
                    return "success";
                default:
                    return "fail";
            }
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
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
     * 申请退款
     */
    @PostMapping("/refund")
    public R<Boolean> refund(@RequestParam String paymentNo,
                           @RequestParam BigDecimal refundAmount,
                           @RequestParam String reason) {
        try {
            boolean result = paymentService.refund(paymentNo, refundAmount, reason);
            return R.ok(result);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}