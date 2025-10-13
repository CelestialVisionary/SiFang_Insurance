package com.sifang.insurance.underwriting.job;

import com.alibaba.fastjson.JSON;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.mapper.UnderwritingRecordMapper;
import com.sifang.insurance.underwriting.service.FeignOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核保结果同步定时任务
 * 定期将已完成的核保记录同步到订单服务
 */
@Component
public class UnderwritingSyncJob {

    private static final Logger logger = LoggerFactory.getLogger(UnderwritingSyncJob.class);

    @Autowired
    private UnderwritingRecordMapper underwritingRecordMapper;

    @Autowired
    private FeignOrderService feignOrderService;

    /**
     * 每30秒执行一次同步任务
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void syncUnderwritingResults() {
        logger.info("开始执行核保结果同步任务");
        
        try {
            // 查询需要同步的核保记录
            List<UnderwritingRecord> records = underwritingRecordMapper.selectNeedSyncRecords();
            
            if (records.isEmpty()) {
                logger.info("暂无需要同步的核保记录");
                return;
            }
            
            logger.info("发现 {} 条需要同步的核保记录", records.size());
            
            // 遍历处理每条记录
            for (UnderwritingRecord record : records) {
                try {
                    // 构建同步数据
                    Map<String, Object> syncData = new HashMap<>();
                    syncData.put("orderId", record.getOrderId());
                    syncData.put("underwritingStatus", record.getStatus());
                    syncData.put("resultReason", record.getResultReason());
                    syncData.put("underwritingTime", record.getUnderwritingTime());
                    
                    logger.info("同步核保结果到订单服务，订单ID: {}, 状态: {}", 
                            record.getOrderId(), record.getStatus());
                    
                    // 调用订单服务更新核保状态
                    boolean syncSuccess = false;
                    try {
                        // 这里使用Feign客户端调用订单服务
                        // 如果订单服务不可用，会抛出异常，进入catch块
                        syncSuccess = feignOrderService.updateUnderwritingStatus(syncData);
                    } catch (Exception e) {
                        // 服务调用失败，记录日志，但不标记为已同步
                        logger.error("调用订单服务失败，订单ID: {}", record.getOrderId(), e);
                        continue;
                    }
                    
                    if (syncSuccess) {
                        // 同步成功，标记为已同步
                        underwritingRecordMapper.markAsSynced(record.getId());
                        logger.info("核保结果同步成功，订单ID: {}", record.getOrderId());
                    } else {
                        logger.warn("核保结果同步失败，订单服务返回失败，订单ID: {}", record.getOrderId());
                    }
                    
                } catch (Exception e) {
                    // 单条记录处理失败，继续处理下一条
                    logger.error("处理核保记录同步失败，记录ID: {}", record.getId(), e);
                }
            }
            
        } catch (Exception e) {
            // 任务执行异常，记录日志
            logger.error("执行核保结果同步任务异常", e);
        }
        
        logger.info("核保结果同步任务执行完成");
    }
}