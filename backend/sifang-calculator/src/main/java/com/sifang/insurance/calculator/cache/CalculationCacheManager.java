package com.sifang.insurance.calculator.cache;

import com.alibaba.fastjson.JSON;
import com.sifang.insurance.calculator.constant.CalculatorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 计算缓存管理器
 */
@Component
public class CalculationCacheManager {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 缓存规则
     */
    public void cacheRule(Long ruleId, Object rule, int expireTime) {
        String key = CalculatorConstants.Cache.RULE_PREFIX + ruleId;
        redisTemplate.opsForValue().set(key, JSON.toJSONString(rule), expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存的规则
     */
    public <T> T getRule(Long ruleId, Class<T> clazz) {
        String key = CalculatorConstants.Cache.RULE_PREFIX + ruleId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? JSON.parseObject(value, clazz) : null;
    }

    /**
     * 移除规则缓存
     */
    public void removeRule(Long ruleId) {
        String key = CalculatorConstants.Cache.RULE_PREFIX + ruleId;
        redisTemplate.delete(key);
    }

    /**
     * 缓存费率
     */
    public void cacheRate(String rateKey, Object rate, int expireTime) {
        String key = CalculatorConstants.Cache.RATE_PREFIX + rateKey;
        redisTemplate.opsForValue().set(key, JSON.toJSONString(rate), expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存的费率
     */
    public <T> T getRate(String rateKey, Class<T> clazz) {
        String key = CalculatorConstants.Cache.RATE_PREFIX + rateKey;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? JSON.parseObject(value, clazz) : null;
    }

    /**
     * 移除费率缓存
     */
    public void removeRate(String rateKey) {
        String key = CalculatorConstants.Cache.RATE_PREFIX + rateKey;
        redisTemplate.delete(key);
    }

    /**
     * 缓存计算结果
     */
    public void cacheResult(String calculateNo, Object result, int expireTime) {
        String key = CalculatorConstants.Cache.RESULT_PREFIX + calculateNo;
        redisTemplate.opsForValue().set(key, JSON.toJSONString(result), expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存的计算结果
     */
    public <T> T getResult(String calculateNo, Class<T> clazz) {
        String key = CalculatorConstants.Cache.RESULT_PREFIX + calculateNo;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? JSON.parseObject(value, clazz) : null;
    }

    /**
     * 移除计算结果缓存
     */
    public void removeResult(String calculateNo) {
        String key = CalculatorConstants.Cache.RESULT_PREFIX + calculateNo;
        redisTemplate.delete(key);
    }

    /**
     * 缓存键是否存在
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置缓存过期时间
     */
    public void expire(String key, int expireTime) {
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }
}