package com.sifang.insurance.underwriting.vo;

import lombok.Data;

/**
 * 核保请求参数
 */
@Data
public class UnderwritingRequest {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 投保信息
     */
    private InsuredInfo insuredInfo;

    /**
     * 被保险人信息
     */
    private InsuredInfo beneficiaryInfo;

    /**
     * 投保信息内部类
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

        /**
         * 性别
         */
        private String gender;

        /**
         * 年龄
         */
        private Integer age;

        /**
         * 职业
         */
        private String occupation;

        /**
         * 健康状况
         */
        private String healthStatus;
    }
}