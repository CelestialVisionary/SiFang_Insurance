package com.sifang.insurance.calculator.controller;

import com.sifang.insurance.calculator.entity.CalculationRule;
import com.sifang.insurance.calculator.service.CalculationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 计算规则控制器
 */
@RestController
@RequestMapping("/api/calculator/rules")
public class CalculationRuleController {

    @Autowired
    private CalculationRuleService calculationRuleService;

    /**
     * 获取所有计算规则
     */
    @GetMapping
    public ResponseEntity<List<CalculationRule>> getAllRules() {
        List<CalculationRule> rules = calculationRuleService.list();
        return ResponseEntity.ok(rules);
    }

    /**
     * 根据ID获取规则
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalculationRule> getRuleById(@PathVariable("id") Long id) {
        CalculationRule rule = calculationRuleService.getById(id);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rule);
    }

    /**
     * 根据产品ID获取启用的规则
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CalculationRule>> getEnabledRulesByProductId(@PathVariable("productId") Long productId) {
        List<CalculationRule> rules = calculationRuleService.getEnabledRulesByProductId(productId);
        return ResponseEntity.ok(rules);
    }

    /**
     * 创建计算规则
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRule(@RequestBody CalculationRule rule) {
        boolean success = calculationRuleService.saveOrUpdateRule(rule);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "规则创建成功", "id", rule.getId()));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "规则创建失败"));
        }
    }

    /**
     * 更新计算规则
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRule(@PathVariable("id") Long id, @RequestBody CalculationRule rule) {
        rule.setId(id);
        boolean success = calculationRuleService.saveOrUpdateRule(rule);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "规则更新成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "规则更新失败"));
        }
    }

    /**
     * 删除计算规则
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRule(@PathVariable("id") Long id) {
        boolean success = calculationRuleService.removeRule(id);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "规则删除成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "规则删除失败"));
        }
    }

    /**
     * 启用规则
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<Map<String, Object>> enableRule(@PathVariable("id") Long id) {
        boolean success = calculationRuleService.enableRule(id);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "规则启用成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "规则启用失败"));
        }
    }

    /**
     * 禁用规则
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Map<String, Object>> disableRule(@PathVariable("id") Long id) {
        boolean success = calculationRuleService.disableRule(id);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "规则禁用成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "规则禁用失败"));
        }
    }
}