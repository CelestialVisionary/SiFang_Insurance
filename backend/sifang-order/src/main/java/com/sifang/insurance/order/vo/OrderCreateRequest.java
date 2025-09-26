package com.sifang.insurance.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建订单请求参数
 */
@Data
public class OrderCreateRequest {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 保费金额
     */
    private BigDecimal premiumAmount;

    /**
     * 保险金额
     */
    private BigDecimal insuranceAmount;

    /**
     * 保险期间（天）
     */
    private Integer insurancePeriod;

    /**
     * 保障开始时间
     */
    private LocalDateTime coverStartTime;

    /**
     * 保障结束时间
     */
    private LocalDateTime coverEndTime;

    /**
     * 投保人信息
     */
    private InsuredInfo insuredInfo;

    /**
     * 被保险人信息
     */
    private InsuredInfo beneficiaryInfo;

    /**
     * 支付方式：1-支付宝，2-微信支付，3-银联支付
     */
    private Integer payMethod;

    /**
     * 投保人信息内部类
     */
    @Data
    public static class InsuredInfo {
        
        /**
         * 姓名
         */
        private String name;

        /**
         * 身份证号
         */
        private String idCard;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 邮箱
         */
        private String email;
    }
}