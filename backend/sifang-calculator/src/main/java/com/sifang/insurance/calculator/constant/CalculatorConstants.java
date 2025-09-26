package com.sifang.insurance.calculator.constant;

/**
 * 计算器常量类
 */
public class CalculatorConstants {

    /**
     * 计算方式枚举
     */
    public static class CalculationMethod {
        /** 固定金额 */
        public static final Integer FIXED_AMOUNT = 1;
        /** 比例计算 */
        public static final Integer RATE_CALCULATION = 2;
        /** 阶梯计算 */
        public static final Integer TIERED_CALCULATION = 3;
        /** 规则引擎 */
        public static final Integer RULE_ENGINE = 4;
    }

    /**
     * 状态枚举
     */
    public static class Status {
        /** 禁用 */
        public static final Integer DISABLED = 0;
        /** 启用 */
        public static final Integer ENABLED = 1;
    }

    /**
     * 计算状态枚举
     */
    public static class CalculationStatus {
        /** 计算成功 */
        public static final Integer SUCCESS = 1;
        /** 计算失败 */
        public static final Integer FAILURE = 2;
        /** 计算中 */
        public static final Integer PROCESSING = 3;
    }

    /**
     * 产品类型枚举
     */
    public static class ProductType {
        /** 意外险 */
        public static final Integer ACCIDENT = 1;
        /** 健康险 */
        public static final Integer HEALTH = 2;
        /** 寿险 */
        public static final Integer LIFE = 3;
        /** 财产险 */
        public static final Integer PROPERTY = 4;
        /** 车险 */
        public static final Integer AUTO = 5;
    }

    /**
     * 风险等级枚举
     */
    public static class RiskLevel {
        /** 低风险 */
        public static final Integer LOW = 1;
        /** 中低风险 */
        public static final Integer MEDIUM_LOW = 2;
        /** 中风险 */
        public static final Integer MEDIUM = 3;
        /** 中高风险 */
        public static final Integer MEDIUM_HIGH = 4;
        /** 高风险 */
        public static final Integer HIGH = 5;
    }

    /**
     * 缓存相关常量
     */
    public static class Cache {
        /** 规则缓存前缀 */
        public static final String RULE_PREFIX = "calculator:rule:";
        /** 费率缓存前缀 */
        public static final String RATE_PREFIX = "calculator:rate:";
        /** 计算结果缓存前缀 */
        public static final String RESULT_PREFIX = "calculator:result:";
        /** 缓存过期时间（秒） */
        public static final Integer DEFAULT_EXPIRE_TIME = 3600;
    }

    /**
     * 错误码
     */
    public static class ErrorCode {
        /** 参数错误 */
        public static final String PARAM_ERROR = "PARAM_ERROR";
        /** 规则不存在 */
        public static final String RULE_NOT_EXIST = "RULE_NOT_EXIST";
        /** 规则未启用 */
        public static final String RULE_NOT_ENABLED = "RULE_NOT_ENABLED";
        /** 计算失败 */
        public static final String CALCULATION_FAILED = "CALCULATION_FAILED";
        /** 规则引擎错误 */
        public static final String RULE_ENGINE_ERROR = "RULE_ENGINE_ERROR";
        /** 数据不存在 */
        public static final String DATA_NOT_EXIST = "DATA_NOT_EXIST";
        /** 系统错误 */
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    }

    /**
     * 计算参数常量
     */
    public static class Param {
        /** 保额 */
        public static final String AMOUNT = "amount";
        /** 年龄 */
        public static final String AGE = "age";
        /** 性别 */
        public static final String GENDER = "gender";
        /** 职业 */
        public static final String OCCUPATION = "occupation";
        /** 地区 */
        public static final String REGION = "region";
        /** 保障期限 */
        public static final String PERIOD = "period";
        /** 缴费期限 */
        public static final String PAYMENT_PERIOD = "paymentPeriod";
        /** 折扣信息 */
        public static final String DISCOUNT_INFO = "discountInfo";
        /** 附加费用信息 */
        public static final String ADDITIONAL_INFO = "additionalInfo";
    }
}