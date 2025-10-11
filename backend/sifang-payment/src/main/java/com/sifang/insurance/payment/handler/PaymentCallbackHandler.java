package com.sifang.insurance.payment.handler;

import com.sifang.insurance.payment.entity.PaymentRecord;
import com.sifang.insurance.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付回调处理器
 * 负责处理第三方支付平台的异步通知
 */
@Component
@Slf4j
public class PaymentCallbackHandler {

    @Autowired
    private PaymentService paymentService;

    /**
     * 处理支付回调
     * @param paymentMethod 支付方式：1-支付宝 2-微信支付 3-银联支付
     * @param request HTTP请求对象
     * @return 处理结果，返回给第三方支付平台
     */
    public String handleCallback(Integer paymentMethod, HttpServletRequest request) {
        try {
            log.info("收到支付回调，支付方式：{}", paymentMethod);
            
            // 解析请求参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            String params = convertParamsToString(parameterMap);
            log.info("回调参数：{}", params);
            
            // 根据不同支付方式进行处理
            boolean success = paymentService.processCallback(paymentMethod, params);
            
            if (success) {
                // 根据不同支付方式返回成功响应
                return getSuccessResponse(paymentMethod);
            } else {
                // 返回失败响应
                return getFailResponse(paymentMethod);
            }
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            return getFailResponse(paymentMethod);
        }
    }

    /**
     * 验证回调签名
     * @param paymentMethod 支付方式
     * @param params 回调参数
     * @return 是否验证通过
     */
    public boolean verifySignature(Integer paymentMethod, Map<String, String> params) {
        // 根据不同支付方式实现签名验证逻辑
        switch (paymentMethod) {
            case 1: // 支付宝
                return verifyAlipaySignature(params);
            case 2: // 微信支付
                return verifyWechatSignature(params);
            case 3: // 银联支付
                return verifyUnionpaySignature(params);
            default:
                log.warn("未知的支付方式：{}", paymentMethod);
                return false;
        }
    }

    /**
     * 验证支付宝签名
     */
    private boolean verifyAlipaySignature(Map<String, String> params) {
        // TODO: 实现支付宝签名验证逻辑
        log.info("验证支付宝签名");
        return true; // 模拟验证通过
    }

    /**
     * 验证微信支付签名
     */
    private boolean verifyWechatSignature(Map<String, String> params) {
        // TODO: 实现微信支付签名验证逻辑
        log.info("验证微信支付签名");
        return true; // 模拟验证通过
    }

    /**
     * 验证银联支付签名
     */
    private boolean verifyUnionpaySignature(Map<String, String> params) {
        // TODO: 实现银联支付签名验证逻辑
        log.info("验证银联支付签名");
        return true; // 模拟验证通过
    }

    /**
     * 获取成功响应
     */
    private String getSuccessResponse(Integer paymentMethod) {
        switch (paymentMethod) {
            case 1: // 支付宝
                return "success";
            case 2: // 微信支付
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            case 3: // 银联支付
                return "{\"respCode\":\"00\",\"respMsg\":\"成功\"}";
            default:
                return "success";
        }
    }

    /**
     * 获取失败响应
     */
    private String getFailResponse(Integer paymentMethod) {
        switch (paymentMethod) {
            case 1: // 支付宝
                return "fail";
            case 2: // 微信支付
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
            case 3: // 银联支付
                return "{\"respCode\":\"99\",\"respMsg\":\"处理失败\"}";
            default:
                return "fail";
        }
    }

    /**
     * 将请求参数转换为字符串
     */
    private String convertParamsToString(Map<String, String[]> parameterMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                sb.append(key).append("=");
                if (values.length == 1) {
                    sb.append(values[0]);
                } else {
                    sb.append("[");
                    for (int i = 0; i < values.length; i++) {
                        sb.append(values[i]);
                        if (i < values.length - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("]");
                }
                sb.append("&");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}