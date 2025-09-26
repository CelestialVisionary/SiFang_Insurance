package com.sifang.insurance.underwriting.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 订单服务Feign客户端降级处理类
 */
@Component
public class OrderFeignClientFallback implements OrderFeignClient {

    private static final Logger logger = LoggerFactory.getLogger(OrderFeignClientFallback.class);

    @Override
    public boolean updateOrderUnderwritingStatus(String orderId, Integer underwritingStatus) {
        logger.error("调用订单服务更新核保状态失败，订单ID: {}, 状态: {}", orderId, underwritingStatus);
        // 返回失败，允许定时任务下次重试
        return false;
    }
}