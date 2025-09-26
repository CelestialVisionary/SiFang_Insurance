package com.sifang.insurance.order.feign;

import com.sifang.insurance.common.entity.ResponseResult;
import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 核保服务Feign客户端
 */
@FeignClient(name = "sifang-underwriting", url = "${feign.client.url.underwriting:http://localhost:8003}")
public interface UnderwritingFeignClient {

    /**
     * 提交核保申请
     */
    @PostMapping("/api/underwriting/submit")
    ResponseResult<UnderwritingResponse> submitUnderwriting(@RequestBody UnderwritingRequest request);

    /**
     * 查询核保结果
     */
    @GetMapping("/api/underwriting/result/{orderId}")
    ResponseResult<UnderwritingRecord> queryUnderwritingResult(@PathVariable("orderId") String orderId);

    /**
     * 人工复核核保
     */
    @PostMapping("/api/underwriting/manual-review")
    ResponseResult<Boolean> manualReview(
            @RequestParam("id") Long id,
            @RequestParam("status") Integer status,
            @RequestParam("reason") String reason,
            @RequestParam("underwriter") String underwriter);
}