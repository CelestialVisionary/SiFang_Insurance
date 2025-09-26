package com.sifang.insurance.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.sifang.insurance.payment.dto.CreatePaymentRequest;
import com.sifang.insurance.payment.dto.PaymentResponse;
import com.sifang.insurance.payment.entity.PaymentRecord;
import com.sifang.insurance.payment.handler.PaymentCallbackHandler;
import com.sifang.insurance.payment.mapper.PaymentRecordMapper;
import com.sifang.insurance.payment.sender.PaymentMessageSender;
import com.sifang.insurance.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private PaymentMessageSender paymentMessageSender;
    
    @Autowired
    private PaymentCallbackHandler paymentCallbackHandler;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        // 检查订单是否已存在支付记录
        PaymentRecord existingRecord = paymentRecordMapper.selectByOrderId(request.getOrderId());
        if (existingRecord != null && existingRecord.getStatus() == 1) {
            throw new RuntimeException("该订单已支付成功");
        }
        
        // 生成支付流水号
        String paymentNo = generatePaymentNo();
        
        // 创建支付记录
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderId(request.getOrderId());
        record.setUserId(request.getUserId());
        record.setAmount(request.getAmount());
        record.setPaymentMethod(request.getPaymentMethod());
        record.setStatus(0); // 待支付
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setRemark(request.getProductName());
        
        paymentRecordMapper.insert(record);
        
        // 调用不同的支付渠道
        PaymentResponse response = new PaymentResponse();
        response.setPaymentNo(paymentNo);
        response.setOrderId(request.getOrderId());
        response.setAmount(request.getAmount());
        response.setStatus(0);
        response.setStatusDesc("待支付");
        
        switch (request.getPaymentMethod()) {
            case 1: // 支付宝
                response.setPaymentMethodName("支付宝");
                // 生成支付宝支付链接（模拟）
                response.setPaymentUrl(generateAlipayUrl(request, paymentNo));
                break;
            case 2: // 微信支付
                response.setPaymentMethodName("微信支付");
                // 生成微信支付二维码（模拟）
                response.setPaymentUrl(generateWechatPayQrcode(request, paymentNo));
                break;
            case 3: // 银联支付
                response.setPaymentMethodName("银联支付");
                // 生成银联支付链接（模拟）
                response.setPaymentUrl(generateUnionpayUrl(request, paymentNo));
                break;
            default:
                throw new RuntimeException("不支持的支付方式");
        }
        
        // 设置过期时间（2小时）
        response.setExpireTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
        
        // 缓存支付信息到Redis，用于支付回调验证
        redisTemplate.opsForValue().set("payment:" + paymentNo, JSON.toJSONString(response), 2, TimeUnit.HOURS);
        
        return response;
    }

    @Override
    @Transactional
    public boolean processCallback(Integer paymentMethod, String params) {
        try {
            log.info("开始处理支付回调，支付方式: {}", paymentMethod);
            
            // 解析回调参数
            Map<String, String> callbackParams = parseCallbackParams(params);
            String paymentNo = callbackParams.get("payment_no");
            
            // 验证回调签名
            boolean signatureValid = paymentCallbackHandler.verifySignature(paymentMethod, callbackParams);
            if (!signatureValid) {
                log.warn("支付回调签名验证失败，支付流水号: {}", paymentNo);
                return false;
            }
            
            // 验证支付流水号
            PaymentRecord record = paymentRecordMapper.selectByPaymentNo(paymentNo);
            if (record == null) {
                log.warn("未找到支付记录，支付流水号: {}", paymentNo);
                return false;
            }
            
            // 验证支付状态
            if (record.getStatus() != 0) {
                log.info("支付记录已处理过，支付流水号: {}, 当前状态: {}", paymentNo, record.getStatus());
                return true; // 已经处理过，直接返回成功
            }
            
            String status = callbackParams.get("status");
            String thirdPartyPaymentNo = callbackParams.get("third_party_payment_no");
            
            // 更新支付记录
            if ("success".equals(status)) {
                log.info("支付成功，更新支付记录，支付流水号: {}", paymentNo);
                record.setStatus(1); // 支付成功
                record.setPaymentTime(new Date());
                record.setThirdPartyPaymentNo(thirdPartyPaymentNo);
                record.setUpdateTime(new Date());
                paymentRecordMapper.updateById(record);
                
                // 发送支付成功消息
                boolean messageSent = paymentMessageSender.sendPaymentSuccessMessage(record);
                if (messageSent) {
                    log.info("支付成功消息发送成功，支付流水号: {}", paymentNo);
                } else {
                    log.error("支付成功消息发送失败，支付流水号: {}", paymentNo);
                    // 可以考虑将失败消息存入重试队列
                }
                
                return true;
            } else {
                log.info("支付失败，更新支付记录，支付流水号: {}", paymentNo);
                record.setStatus(2); // 支付失败
                record.setUpdateTime(new Date());
                paymentRecordMapper.updateById(record);
                // 发送支付失败消息
                sendPaymentFailedMessage(record);
                return false;
            }
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            throw e;
        }
    }

    @Override
    public PaymentRecord queryPaymentStatus(String paymentNo) {
        log.info("查询支付状态，支付流水号: {}", paymentNo);
        return paymentRecordMapper.selectByPaymentNo(paymentNo);
    }

    @Override
    public PaymentRecord queryPaymentByOrderId(String orderId) {
        log.info("根据订单ID查询支付记录，订单ID: {}", orderId);
        return paymentRecordMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional
    public boolean refund(String paymentNo, BigDecimal refundAmount, String reason) {
        try {
            log.info("发起退款请求，支付流水号: {}, 退款金额: {}, 退款原因: {}", paymentNo, refundAmount, reason);
            
            PaymentRecord record = paymentRecordMapper.selectByPaymentNo(paymentNo);
            if (record == null) {
                log.warn("未找到支付记录，支付流水号: {}", paymentNo);
                return false;
            }
            
            // 检查支付状态
            if (record.getStatus() != 1) {
                log.error("只有支付成功的订单才能退款，支付流水号: {}, 当前状态: {}", paymentNo, record.getStatus());
                throw new RuntimeException("只有支付成功的订单才能退款");
            }
            
            // 检查退款金额
            if (refundAmount.compareTo(record.getAmount()) > 0) {
                log.error("退款金额不能大于支付金额，支付流水号: {}, 支付金额: {}, 退款金额: {}", 
                        paymentNo, record.getAmount(), refundAmount);
                throw new RuntimeException("退款金额不能大于支付金额");
            }
            
            // 更新支付状态为退款中
            record.setStatus(3);
            record.setUpdateTime(new Date());
            paymentRecordMapper.updateById(record);
            
            // 调用第三方退款接口
            boolean refundResult = callThirdPartyRefund(record.getPaymentMethod(), paymentNo, refundAmount, reason);
            
            if (refundResult) {
                // 更新为已退款
                log.info("退款成功，更新支付记录状态，支付流水号: {}", paymentNo);
                record.setStatus(4);
                record.setUpdateTime(new Date());
                paymentRecordMapper.updateById(record);
                
                // 发送退款成功消息
                boolean messageSent = paymentMessageSender.sendRefundSuccessMessage(record);
                if (messageSent) {
                    log.info("退款成功消息发送成功，支付流水号: {}", paymentNo);
                } else {
                    log.error("退款成功消息发送失败，支付流水号: {}", paymentNo);
                }
            } else {
                // 恢复支付状态
                record.setStatus(1);
                record.setUpdateTime(new Date());
                paymentRecordMapper.updateById(record);
                log.error("退款失败，恢复支付记录状态，支付流水号: {}", paymentNo);
            }
            
            return refundResult;
        } catch (Exception e) {
            log.error("处理退款异常，支付流水号: {}", paymentNo, e);
            throw e;
        }
    }
    
    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "PAY" + sdf.format(new Date()) + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 生成支付宝支付链接（模拟）
     */
    private String generateAlipayUrl(CreatePaymentRequest request, String paymentNo) {
        // 实际项目中应该调用支付宝SDK生成支付链接
        return "https://openapi.alipay.com/gateway.do?out_trade_no=" + paymentNo + "&total_amount=" + request.getAmount();
    }
    
    /**
     * 生成微信支付二维码（模拟）
     */
    private String generateWechatPayQrcode(CreatePaymentRequest request, String paymentNo) {
        // 实际项目中应该调用微信支付SDK生成支付二维码
        return "weixin://wxpay/bizpayurl?pr=f8k3Zu";
    }
    
    /**
     * 生成银联支付链接（模拟）
     */
    private String generateUnionpayUrl(CreatePaymentRequest request, String paymentNo) {
        // 实际项目中应该调用银联支付SDK生成支付链接
        return "https://gateway.95516.com/gateway/api/frontTransReq.do?orderId=" + paymentNo + "&txnAmt=" + request.getAmount().multiply(new BigDecimal(100)).intValue();
    }
    
    /**
     * 解析回调参数
     */
    private Map<String, String> parseCallbackParams(String params) {
        log.debug("解析回调参数: {}", params);
        // 实际项目中应该根据不同支付渠道的回调格式进行解析
        Map<String, String> result = new HashMap<>();
        try {
            // 这里只是模拟解析
            String[] pairs = params.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    result.put(keyValue[0], keyValue[1]);
                }
            }
        } catch (Exception e) {
            log.error("解析回调参数异常", e);
        }
        return result;
    }
    
    /**
     * 调用第三方退款接口
     */
    private boolean callThirdPartyRefund(Integer paymentMethod, String paymentNo, BigDecimal refundAmount, String reason) {
        log.info("调用第三方退款接口，支付方式: {}, 支付流水号: {}, 退款金额: {}", 
                paymentMethod, paymentNo, refundAmount);
        try {
            // 实际项目中应该调用对应的第三方支付SDK进行退款
            // 根据不同支付方式调用不同的退款API
            switch (paymentMethod) {
                case 1: // 支付宝
                    // 调用支付宝退款API
                    log.debug("调用支付宝退款API");
                    break;
                case 2: // 微信支付
                    // 调用微信支付退款API
                    log.debug("调用微信支付退款API");
                    break;
                case 3: // 银联支付
                    // 调用银联支付退款API
                    log.debug("调用银联支付退款API");
                    break;
                default:
                    log.error("不支持的支付方式: {}", paymentMethod);
                    return false;
            }
            
            // 这里只是模拟退款成功
            return true;
        } catch (Exception e) {
            log.error("调用第三方退款接口异常", e);
            return false;
        }
    }
    
    /**
     * 发送支付失败消息
     */
    private void sendPaymentFailedMessage(PaymentRecord record) {
        log.info("发送支付失败消息，支付流水号: {}", record.getPaymentNo());
        boolean messageSent = paymentMessageSender.sendPaymentFailedMessage(record, "支付失败");
        if (!messageSent) {
            log.error("支付失败消息发送失败，支付流水号: {}", record.getPaymentNo());
        }
    }
}