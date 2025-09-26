package com.sifang.insurance.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
    
    /**
     * 根据订单ID查询支付记录
     * @param orderId 订单ID
     * @return 支付记录
     */
    PaymentRecord selectByOrderId(String orderId);
    
    /**
     * 根据支付流水号查询支付记录
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    PaymentRecord selectByPaymentNo(String paymentNo);
    
    /**
     * 根据用户ID查询支付记录列表
     * @param userId 用户ID
     * @return 支付记录列表
     */
    List<PaymentRecord> selectByUserId(Long userId);
}