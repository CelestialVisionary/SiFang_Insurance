package com.sifang.insurance.underwriting.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单服务Feign客户端
 * 用于核保服务调用订单服务接口
 */
@FeignClient(name = "sifang-order", fallback = OrderFeignClientFallback.class)
public interface OrderFeignClient {

    /**
     * 更新订单核保状态
     * @param orderId 订单ID
     * @param underwritingStatus 核保状态
     * @return 是否更新成功
     */
    @PostMapping("/api/order/underwriting/status")
    boolean updateOrderUnderwritingStatus(@RequestParam("orderId") String orderId, 
                                         @RequestParam("underwritingStatus") Integer underwritingStatus);
}