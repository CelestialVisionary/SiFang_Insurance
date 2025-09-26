package com.sifang.insurance.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.payment.entity.PaymentMethod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 支付方式Mapper接口
 */
@Mapper
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod> {
    
    /**
     * 查询启用的支付方式列表
     * @return 支付方式列表
     */
    List<PaymentMethod> selectEnabledPaymentMethods();
    
    /**
     * 根据支付方式编码查询
     * @param methodCode 支付方式编码
     * @return 支付方式
     */
    PaymentMethod selectByMethodCode(String methodCode);
}