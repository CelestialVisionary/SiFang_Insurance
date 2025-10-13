package com.sifang.insurance.underwriting.controller;

import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.service.UnderwritingRuleService;
import com.sifang.insurance.underwriting.common.vo.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 核保规则管理控制器
 */
@RestController
@RequestMapping("/api/underwriting/rule")
@Api(tags = "核保规则管理接口")
public class UnderwritingRuleController {

    @Autowired
    private UnderwritingRuleService underwritingRuleService;

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
    public ResponseResult<Boolean> saveRule(@RequestBody UnderwritingRule rule) {
        boolean result = underwritingRuleService.saveRule(rule);
        if (result) {
            return ResponseResult.success(true, "保存成功");
        }
        return ResponseResult.fail("保存失败");
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
}