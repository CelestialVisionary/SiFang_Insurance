package com.sifang.insurance.underwriting.controller;

import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.service.UnderwritingRecordService;
import com.sifang.insurance.underwriting.common.vo.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 核保记录管理控制器
 */
@RestController
@RequestMapping("/api/underwriting/record")
@Api(tags = "核保记录管理接口")
public class UnderwritingRecordController {

    @Autowired
    private UnderwritingRecordService underwritingRecordService;

    /**
     * 分页查询核保记录
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询核保记录", notes = "根据条件分页查询核保记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页数量", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品ID", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "核保状态：0-待核保 1-核保通过 2-核保拒绝 3-人工复核中", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "String", paramType = "query")
    })
    public ResponseResult<Map<String, Object>> pageQuery(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("orderId", orderId);
        params.put("userId", userId);
        params.put("productId", productId);
        params.put("status", status);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        Map<String, Object> result = underwritingRecordService.pageQuery(params);
        return ResponseResult.success(result);
    }

    /**
     * 根据ID获取核保记录详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取核保记录详情", notes = "根据ID获取核保记录详细信息")
    @ApiImplicitParam(name = "id", value = "核保记录ID", required = true, dataType = "Long", paramType = "path")
    public ResponseResult<UnderwritingRecord> getById(@PathVariable Long id) {
        UnderwritingRecord record = underwritingRecordService.getById(id);
        if (record == null) {
            return ResponseResult.fail("核保记录不存在");
        }
        return ResponseResult.success(record);
    }

    /**
     * 获取核保统计信息
     */
    @GetMapping("/statistics")
    @ApiOperation(value = "获取核保统计信息", notes = "获取核保记录的统计信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "String", paramType = "query")
    })
    public ResponseResult<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        Map<String, Object> statistics = underwritingRecordService.getStatistics(params);
        return ResponseResult.success(statistics);
    }
}