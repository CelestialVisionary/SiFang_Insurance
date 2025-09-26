package com.sifang.insurance.order.job;

import com.sifang.insurance.common.entity.ResponseResult;
import com.sifang.insurance.order.entity.Order;
import com.sifang.insurance.order.feign.UnderwritingFeignClient;
import com.sifang.insurance.order.service.OrderService;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 核保状态同步定时任务
 */
@Component
public class UnderwritingStatusSyncJob {

    private static final Logger logger = LoggerFactory.getLogger(UnderwritingStatusSyncJob.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UnderwritingFeignClient underwritingFeignClient;

    /**
     * 每5分钟同步一次待核保和人工复核中的订单状态
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncUnderwritingStatus() {
        logger.info("开始同步核保状态...");
        
        try {
            // 查询待核保和人工复核中的订单
            List<Order> orders = orderService.getOrdersByUnderwritingStatus(List.of(0, 3));
            
            for (Order order : orders) {
                try {
                    // 调用核保服务查询最新状态
                    ResponseResult<UnderwritingRecord> result = underwritingFeignClient.queryUnderwritingResult(order.getOrderNo());
                    
                    if (result != null && result.getCode() == 200 && result.getData() != null) {
                        UnderwritingRecord underwritingRecord = result.getData();
                        Integer latestStatus = underwritingRecord.getStatus();
                        
                        // 如果状态有变更，则更新订单
                        if (!order.getUnderwritingStatus().equals(latestStatus)) {
                            logger.info("订单[{}]核保状态发生变更，从[{}]更新为[{}]", 
                                    order.getOrderNo(), order.getUnderwritingStatus(), latestStatus);
                            
                            // 更新核保状态
                            orderService.updateUnderwritingStatus(order.getId(), latestStatus);
                            
                            // 如果核保拒绝，则取消订单
                            if (latestStatus == 2) {
                                orderService.updateOrderStatus(order.getId(), 2); // 已取消
                                logger.info("订单[{}]因核保拒绝已取消", order.getOrderNo());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("同步订单[{}]核保状态失败", order.getOrderNo(), e);
                }
            }
            
            logger.info("核保状态同步完成，共处理订单数: {}", orders.size());
        } catch (Exception e) {
            logger.error("核保状态同步任务执行失败", e);
        }
    }
}