package com.sifang.insurance.calculator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.calculator.entity.PremiumRule;
import com.sifang.insurance.calculator.mapper.PremiumRuleMapper;
import com.sifang.insurance.calculator.service.PremiumRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 保费计算规则服务实现类
 */
@Service
public class PremiumRuleServiceImpl extends ServiceImpl<PremiumRuleMapper, PremiumRule> implements PremiumRuleService {

    @Autowired
    private PremiumRuleMapper premiumRuleMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RULES_CACHE_KEY = "premium:rules:";
    private static final Long CACHE_EXPIRE_TIME = 10L; // 10分钟

    @Override
    public List<PremiumRule> getActiveRulesByProductId(Long productId) {
        String cacheKey = RULES_CACHE_KEY + "product:" + productId;
        List<PremiumRule> rules = (List<PremiumRule>) redisTemplate.opsForValue().get(cacheKey);
        if (rules == null) {
            rules = premiumRuleMapper.selectByProductIdAndStatus(productId, 1);
            if (rules != null && !rules.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, rules, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            }
        }
        return rules;
    }

    @Override
    public List<PremiumRule> getRulesByProductIdAndType(Long productId, Integer ruleType) {
        return premiumRuleMapper.selectByProductIdAndType(productId, ruleType);
    }

    @Override
    public boolean createRule(PremiumRule rule) {
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        boolean result = save(rule);
        if (result) {
            // 清除缓存
            clearProductRulesCache(rule.getProductId());
        }
        return result;
    }

    @Override
    public boolean updateRule(PremiumRule rule) {
        rule.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(rule);
        if (result) {
            // 清除缓存
            clearProductRulesCache(rule.getProductId());
        }
        return result;
    }

    @Override
    public boolean updateRuleStatus(Long id, Integer status) {
        PremiumRule rule = getById(id);
        if (rule != null) {
            rule.setStatus(status);
            rule.setUpdateTime(LocalDateTime.now());
            boolean result = updateById(rule);
            if (result) {
                // 清除缓存
                clearProductRulesCache(rule.getProductId());
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean deleteRule(Long id) {
        PremiumRule rule = getById(id);
        boolean result = removeById(id);
        if (result && rule != null) {
            // 清除缓存
            clearProductRulesCache(rule.getProductId());
        }
        return result;
    }

    /**
     * 清除产品规则缓存
     */
    private void clearProductRulesCache(Long productId) {
        String cacheKey = RULES_CACHE_KEY + "product:" + productId;
        redisTemplate.delete(cacheKey);
    }
}