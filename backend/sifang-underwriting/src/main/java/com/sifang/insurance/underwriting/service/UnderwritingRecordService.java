package com.sifang.insurance.underwriting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;

import java.util.Map;

/**
 * 核保记录服务接口
 */
public interface UnderwritingRecordService extends IService<UnderwritingRecord> {
    
    /**
     * 分页查询核保记录
     * @param params 查询参数
     * @return 分页结果
     */
    Map<String, Object> pageQuery(Map<String, Object> params);
    
    /**
     * 获取核保统计信息
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getStatistics(Map<String, Object> params);
    
    /**
     * 根据订单ID查询核保记录
     * @param orderId 订单ID
     * @return 核保记录
     */
    UnderwritingRecord selectByOrderId(String orderId);
    
    /**
     * 更新同步状态
     * @param id 核保记录ID
     * @param syncStatus 同步状态：0-待同步 1-已同步
     * @return 是否更新成功
     */
    boolean updateSyncStatus(Long id, Integer syncStatus);
}