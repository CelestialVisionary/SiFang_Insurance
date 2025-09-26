package com.sifang.insurance.underwriting.job;

import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.mapper.UnderwritingRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 核保状态同步定时任务
 * 负责将核保状态变更同步到订单服务
 */
@Component
public class UnderwritingStatusSyncJob {

    private static final Logger logger = LoggerFactory.getLogger(UnderwritingStatusSyncJob.class);

    @Autowired
    private UnderwritingRecordMapper underwritingRecordMapper;
    
    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 每5分钟执行一次，同步待同步的核保状态
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void syncUnderwritingStatus() {
        logger.info("开始同步核保状态到订单服务");
        
        try {
            // 查询需要同步的核保记录（状态为1-核保通过，2-核保拒绝，3-人工复核中）
            List<UnderwritingRecord> records = underwritingRecordMapper.selectNeedSyncRecords();
            
            for (UnderwritingRecord record : records) {
                try {
                    // 调用订单服务更新核保状态
                    boolean success = orderFeignClient.updateOrderUnderwritingStatus(record.getOrderId(), record.getStatus());
                    
                    if (success) {
                        // 标记为已同步
                        underwritingRecordMapper.markAsSynced(record.getId());
                        logger.info("订单[{}]核保状态同步成功，状态：{}", record.getOrderId(), record.getStatus());
                    } else {
                        logger.error("订单[{}]核保状态同步失败", record.getOrderId());
                    }
                } catch (Exception e) {
                    logger.error("同步订单[{}]核保状态发生异常", record.getOrderId(), e);
                }
            }
            
            logger.info("核保状态同步完成，共处理 {} 条记录", records.size());
        } catch (Exception e) {
            logger.error("核保状态同步任务执行异常", e);
        }
    }
}