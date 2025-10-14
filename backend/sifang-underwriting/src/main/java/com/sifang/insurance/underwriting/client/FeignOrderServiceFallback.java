package com.sifang.insurance.underwriting.client;

import com.sifang.insurance.underwriting.common.vo.ResponseResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

/**
 * 订单服务Feign客户端降级实现
 */
@Component
public class FeignOrderServiceFallback implements FeignOrderService {
    
    private static final Logger logger = Logger.getLogger(FeignOrderServiceFallback.class.getName());

    @Override
    public ResponseResult<Boolean> updateOrderUnderwritingStatus(String orderId, Integer underwritingStatus, String underwritingResult) {
        logger.warning("订单服务不可用，降级处理：更新订单核保状态失败，订单ID：" + orderId);
        // 返回降级结果，但不抛出异常，让核保流程继续执行
        return ResponseResult.fail("订单服务暂时不可用，稍后将重试更新订单状态");
    }

    @Override
    public ResponseResult<Map<String, Object>> getOrderDetail(String orderId) {
        logger.warning("订单服务不可用，降级处理：获取订单详情失败，订单ID：" + orderId);
        return ResponseResult.fail("订单服务暂时不可用，请稍后重试");
    }
}