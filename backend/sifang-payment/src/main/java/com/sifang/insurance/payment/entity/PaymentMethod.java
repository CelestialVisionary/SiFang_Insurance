package com.sifang.insurance.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付方式实体类
 */
@Data
@TableName("payment_method")
public class PaymentMethod implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 支付方式名称
    private String methodName;
    
    // 支付方式编码：ALIPAY-支付宝 WECHAT-微信支付 UNIONPAY-银联支付
    private String methodCode;
    
    // 支付类型：1-在线支付 2-线下支付
    private Integer paymentType;
    
    // 配置参数（JSON格式）
    private String configParams;
    
    // 排序
    private Integer sort;
    
    // 状态：0-禁用 1-启用
    private Integer status;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;
}