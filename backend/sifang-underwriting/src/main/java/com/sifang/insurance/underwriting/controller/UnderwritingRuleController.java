package com.sifang.insurance.underwriting.controller;

import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.entity.UnderwritingRuleVersion;
import com.sifang.insurance.underwriting.service.UnderwritingRuleService;
import com.sifang.insurance.underwriting.service.UnderwritingRuleVersionService;
import com.sifang.insurance.underwriting.common.vo.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 核保规则管理控制器
 */
@RestController
@RequestMapping("/api/underwriting/rule")
@Api(tags = "核保规则管理接口")
public class UnderwritingRuleController {

    @Autowired
    private UnderwritingRuleService underwritingRuleService;
    
    @Autowired
    private UnderwritingRuleVersionService underwritingRuleVersionService;

    /**
     * 根据产品ID获取启用的核保规则列表
     */
    @GetMapping("/list/{productId}")
    @ApiOperation(value = "获取产品核保规则", notes = "根据产品ID获取启用的核保规则列表")
    @ApiImplicitParam(name = "productId", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<List<UnderwritingRule>> getEnabledRulesByProductId(@PathVariable Long productId) {
        List<UnderwritingRule> rules = underwritingRuleService.getEnabledRulesByProductId(productId);
        return ResponseResult.success(rules);
    }

    /**
     * 保存核保规则
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存核保规则", notes = "新增或修改核保规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rule", value = "规则对象", required = true, dataType = "UnderwritingRule", paramType = "body"),
            @ApiImplicitParam(name = "versionRemark", value = "版本说明", required = false, dataType = "String", paramType = "query")
    })
    public ResponseResult<Boolean> saveRule(@RequestBody UnderwritingRule rule, @RequestParam(required = false) String versionRemark) {
        try {
            // 设置操作人（实际应用中应从当前登录用户获取）
            String operator = "admin";
            
            boolean result = underwritingRuleService.saveRule(rule, versionRemark, operator);
            if (result) {
                return ResponseResult.success(true, "保存成功");
            }
            return ResponseResult.fail("保存失败");
        } catch (IllegalArgumentException e) {
            // 捕获规则语法错误异常
            return ResponseResult.fail(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            return ResponseResult.fail("保存失败: " + e.getMessage());
        }
    }

    /**
     * 更新核保规则状态
     */
    @PostMapping("/status")
    @ApiOperation(value = "更新规则状态", notes = "启用或禁用核保规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "规则ID", required = true, dataType = "Long", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "状态：0-禁用 1-启用", required = true, dataType = "Integer", paramType = "form")
    })
    public ResponseResult<Boolean> updateRuleStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean result = underwritingRuleService.updateRuleStatus(id, status);
        if (result) {
            return ResponseResult.success(true, "状态更新成功");
        }
        return ResponseResult.fail("状态更新失败，规则不存在");
    }

    /**
     * 删除核保规则
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除核保规则", notes = "根据规则ID删除核保规则")
    @ApiImplicitParam(name = "id", value = "规则ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<Boolean> deleteRule(@PathVariable Long id) {
        boolean result = underwritingRuleService.deleteRule(id);
        if (result) {
            return ResponseResult.success(true, "删除成功");
        }
        return ResponseResult.fail("删除失败，规则不存在");
    }

    /**
     * 根据ID获取核保规则详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取规则详情", notes = "根据规则ID获取核保规则详细信息")
    @ApiImplicitParam(name = "id", value = "规则ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<UnderwritingRule> getRuleById(@PathVariable Long id) {
        UnderwritingRule rule = underwritingRuleService.getById(id);
        if (rule == null) {
            return ResponseResult.fail("规则不存在");
        }
        return ResponseResult.success(rule);
    }
    
    /**
     * 验证核保规则语法
     */
    @PostMapping("/validate")
    @ApiOperation(value = "验证规则语法", notes = "验证核保规则的语法正确性")
    @ApiImplicitParam(name = "ruleContent", value = "规则内容", required = true, dataType = "String", paramType = "form")
    public ResponseResult<Map<String, Object>> validateRuleSyntax(@RequestParam String ruleContent) {
        Map<String, Object> validateResult = underwritingRuleService.validateRuleSyntax(ruleContent);
        return ResponseResult.success(validateResult);
    }
    
    /**
     * 获取规则版本列表
     */
    @GetMapping("/versions/{ruleId}")
    @ApiOperation(value = "获取规则版本列表", notes = "根据规则ID获取所有版本历史")
    @ApiImplicitParam(name = "ruleId", value = "规则ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<List<UnderwritingRuleVersion>> getRuleVersions(@PathVariable Long ruleId) {
        List<UnderwritingRuleVersion> versions = underwritingRuleVersionService.getVersionsByRuleId(ruleId);
        return ResponseResult.success(versions);
    }
    
    /**
     * 获取版本详情
     */
    @GetMapping("/version/detail/{versionId}")
    @ApiOperation(value = "获取版本详情", notes = "根据版本ID获取版本详细信息")
    @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<UnderwritingRuleVersion> getVersionDetail(@PathVariable Long versionId) {
        UnderwritingRuleVersion version = underwritingRuleVersionService.getVersionById(versionId);
        if (version == null) {
            return ResponseResult.fail("版本不存在");
        }
        return ResponseResult.success(version);
    }
    
    /**
     * 回滚到指定版本
     */
    @PostMapping("/version/rollback/{versionId}")
    @ApiOperation(value = "回滚到指定版本", notes = "将规则回滚到指定的历史版本")
    @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<Boolean> rollbackToVersion(@PathVariable Long versionId) {
        try {
            boolean result = underwritingRuleVersionService.rollbackToVersion(versionId);
            if (result) {
                return ResponseResult.success(true, "回滚成功");
            }
            return ResponseResult.fail("回滚失败，版本不存在");
        } catch (Exception e) {
            return ResponseResult.fail("回滚失败: " + e.getMessage());
        }
    }
}