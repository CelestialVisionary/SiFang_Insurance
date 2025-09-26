package com.sifang.insurance.underwriting.controller;

import com.sifang.insurance.common.entity.ResponseResult;
import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.service.UnderwritingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 核保服务控制器
 */
@RestController
@RequestMapping("/api/underwriting")
public class UnderwritingController {

    @Autowired
    private UnderwritingService underwritingService;

    /**
     * 提交核保申请
     */
    @PostMapping("/submit")
    public ResponseResult<UnderwritingResponse> submitUnderwriting(@Valid @RequestBody UnderwritingRequest request) {
        UnderwritingResponse response = underwritingService.submitUnderwriting(request);
        return ResponseResult.success(response);
    }

    /**
     * 查询核保结果
     */
    @GetMapping("/result/{orderId}")
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
    public ResponseResult<Boolean> manualReview(
            @RequestParam Long id,
            @RequestParam Integer status,
            @RequestParam String reason,
            @RequestParam String underwriter) {
        boolean result = underwritingService.manualReview(id, status, reason, underwriter);
        if (result) {
            return ResponseResult.success(true);
        } else {
            return ResponseResult.fail("复核失败");
        }
    }
}