package com.sifang.insurance.calculator.controller;

import com.sifang.insurance.calculator.dto.RuleRequest;
import com.sifang.insurance.calculator.entity.CalculationRule;
import com.sifang.insurance.calculator.service.CalculationRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 规则管理控制器
 */
@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private CalculationRuleService calculationRuleService;

    /**
     * 获取规则列表
     */
    @GetMapping("/list")
    public List<CalculationRule> getRuleList() {
        return calculationRuleService.list();
    }

    /**
     * 根据ID获取规则
     */
    @GetMapping("/{id}")
    public CalculationRule getRule(@PathVariable Long id) {
        return calculationRuleService.getById(id);
    }

    /**
     * 根据产品ID获取启用的规则
     */
    @GetMapping("/product/{productId}")
    public List<CalculationRule> getEnabledRulesByProductId(@PathVariable Long productId) {
        return calculationRuleService.getEnabledRulesByProductId(productId);
    }

    /**
     * 根据产品类型获取规则
     */
    @GetMapping("/product-type/{productType}")
    public List<CalculationRule> getRulesByProductType(@PathVariable Integer productType) {
        return calculationRuleService.getRulesByProductType(productType);
    }

    /**
     * 新增规则
     */
    @PostMapping
    public boolean addRule(@RequestBody RuleRequest request) {
        CalculationRule rule = new CalculationRule();
        BeanUtils.copyProperties(request, rule);
        return calculationRuleService.saveOrUpdateRule(rule);
    }

    /**
     * 更新规则
     */
    @PutMapping
    public boolean updateRule(@RequestBody RuleRequest request) {
        CalculationRule rule = new CalculationRule();
        BeanUtils.copyProperties(request, rule);
        return calculationRuleService.saveOrUpdateRule(rule);
    }

    /**
     * 启用规则
     */
    @PutMapping("/{id}/enable")
    public boolean enableRule(@PathVariable Long id) {
        return calculationRuleService.enableRule(id);
    }

    /**
     * 禁用规则
     */
    @PutMapping("/{id}/disable")
    public boolean disableRule(@PathVariable Long id) {
        return calculationRuleService.disableRule(id);
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/{id}")
    public boolean deleteRule(@PathVariable Long id) {
        return calculationRuleService.removeRule(id);
    }
}