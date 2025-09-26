package com.sifang.insurance.calculator.util;

import com.sifang.insurance.calculator.exception.CalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * 计算工具类
 */
public class CalculationUtil {

    /**
     * 默认保留小数位数
     */
    private static final int DEFAULT_SCALE = 2;

    /**
     * 金额格式化
     */
    private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#.00");

    /**
     * 精确加法
     */
    public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
        return value1.add(value2).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 精确减法
     */
    public static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
        return value1.subtract(value2).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 精确乘法
     */
    public static BigDecimal multiply(BigDecimal value1, BigDecimal value2) {
        return value1.multiply(value2).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 精确除法
     */
    public static BigDecimal divide(BigDecimal value1, BigDecimal value2) {
        if (value2.compareTo(BigDecimal.ZERO) == 0) {
            throw new CalculationException("DIV001", "除数不能为零");
        }
        return value1.divide(value2, DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 计算保费（保额 × 费率）
     */
    public static BigDecimal calculatePremium(BigDecimal amount, BigDecimal rate) {
        return multiply(amount, rate);
    }

    /**
     * 应用折扣
     */
    public static BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountRate) {
        BigDecimal actualDiscountRate = discountRate.divide(new BigDecimal(100), DEFAULT_SCALE, RoundingMode.HALF_UP);
        return multiply(amount, new BigDecimal(1).subtract(actualDiscountRate));
    }

    /**
     * 应用附加费用
     */
    public static BigDecimal applyAdditionalFee(BigDecimal amount, BigDecimal additionalFee) {
        return add(amount, additionalFee);
    }

    /**
     * 按阶梯计算
     */
    public static BigDecimal calculateTieredPremium(double amount, Map<Double, Double> tierRates) {
        BigDecimal premium = BigDecimal.ZERO;
        double lastTier = 0;

        // 排序阶梯值
        for (Map.Entry<Double, Double> entry : tierRates.entrySet()) {
            double tier = entry.getKey();
            double rate = entry.getValue();

            if (amount > lastTier) {
                double tierAmount = Math.min(amount, tier) - lastTier;
                premium = add(premium, new BigDecimal(tierAmount).multiply(new BigDecimal(rate)));
                lastTier = tier;
            } else {
                break;
            }
        }

        // 处理超出最高阶梯的部分
        if (amount > lastTier) {
            double maxRate = 0;
            for (double rate : tierRates.values()) {
                maxRate = Math.max(maxRate, rate);
            }
            double remainingAmount = amount - lastTier;
            premium = add(premium, new BigDecimal(remainingAmount).multiply(new BigDecimal(maxRate)));
        }

        return premium;
    }

    /**
     * 格式化金额
     */
    public static String formatAmount(BigDecimal amount) {
        return AMOUNT_FORMAT.format(amount);
    }

    /**
     * 校验数值是否在有效范围内
     */
    public static boolean isValidRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return false;
        }
        boolean minValid = (min == null) || (value.compareTo(min) >= 0);
        boolean maxValid = (max == null) || (value.compareTo(max) <= 0);
        return minValid && maxValid;
    }

    /**
     * 四舍五入到指定小数位
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 将double转换为BigDecimal
     */
    public static BigDecimal toBigDecimal(double value) {
        return new BigDecimal(String.valueOf(value)).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 将字符串转换为BigDecimal
     */
    public static BigDecimal toBigDecimal(String value) {
        try {
            return new BigDecimal(value).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new CalculationException("NUM001", "数字格式错误: " + value, e);
        }
    }
}