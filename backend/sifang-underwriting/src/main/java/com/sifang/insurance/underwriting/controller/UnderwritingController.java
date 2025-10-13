package com.sifang.insurance.underwriting.controller;

import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.service.UnderwritingService;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.common.vo.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 核保服务控制器
 */
@RestController
@RequestMapping("/api/underwriting")
@Api(tags = "核保服务接口")
public class UnderwritingController {

    @Autowired
    private UnderwritingService underwritingService;

    /**
     * 提交核保申请
     */
    @PostMapping("/submit")
    @ApiOperation(value = "提交核保申请", notes = "提交订单核保请求，执行核保流程")
    public ResponseResult<UnderwritingResponse> submitUnderwriting(@Valid @RequestBody UnderwritingRequest request) {
        UnderwritingResponse response = underwritingService.submitUnderwriting(request);
        return ResponseResult.success(response);
    }

    /**
     * 查询核保结果
     */
    @GetMapping("/result/{orderId}")
    @ApiOperation(value = "查询核保结果", notes = "根据订单ID查询核保结果")
    @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "String", paramType = "path")
    public ResponseResult<UnderwritingRecord> queryUnderwritingResult(@PathVariable String orderId) {
        UnderwritingRecord record = underwritingService.queryUnderwritingResult(orderId);
        if (record == null) {
            return ResponseResult.fail("核保记录不存在");
        }
        return ResponseResult.success(record);
    }

    /**
     * 人工复核核保
     */
    @PostMapping("/manual-review")
    @ApiOperation(value = "人工复核核保", notes = "人工复核核保记录并更新状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "核保记录ID", required = true, dataType = "Long", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "复核状态：1-核保通过 2-核保拒绝", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "reason", value = "复核原因", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "underwriter", value = "复核人", required = true, dataType = "String", paramType = "form")
    })
    public ResponseResult<Boolean> manualReview(
            @RequestParam Long id,
            @RequestParam Integer status,
            @RequestParam String reason,
            @RequestParam String underwriter) {
        boolean result = underwritingService.manualReview(id, status, reason, underwriter);
        if (result) {
            return ResponseResult.success(true, "人工复核成功");
        }
        return ResponseResult.fail("人工复核失败，核保记录不存在");
    }
}