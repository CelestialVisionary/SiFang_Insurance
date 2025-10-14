package com.sifang.insurance.underwriting.client;

import com.sifang.insurance.underwriting.common.vo.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 订单服务Feign客户端
 * 用于调用订单服务的API接口，更新订单的核保状态
 */
@FeignClient(name = "sifang-order", fallback = FeignOrderServiceFallback.class)
public interface FeignOrderService {

    /**
     * 更新订单的核保状态
     * @param orderId 订单ID
     * @param underwritingStatus 核保状态
     * @param underwritingResult 核保结果详情（JSON字符串格式）
     * @return 响应结果
     */
    @PostMapping("/api/order/underwriting/status")
    ResponseResult<Boolean> updateOrderUnderwritingStatus(
            @RequestParam("orderId") String orderId,
            @RequestParam("underwritingStatus") Integer underwritingStatus,
            @RequestBody String underwritingResult);

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @PostMapping("/api/order/detail")
    ResponseResult<Map<String, Object>> getOrderDetail(@RequestParam("orderId") String orderId);
}