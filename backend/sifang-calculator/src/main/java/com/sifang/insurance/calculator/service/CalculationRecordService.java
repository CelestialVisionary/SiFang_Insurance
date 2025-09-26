package com.sifang.insurance.calculator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.calculator.entity.CalculationRecord;

import java.util.List;

/**
 * 计算记录服务接口
 */
public interface CalculationRecordService extends IService<CalculationRecord> {

    /**
     * 根据计算流水号获取记录
     */
    CalculationRecord getByCalculateNo(String calculateNo);

    /**
     * 获取用户的计算记录
     */
    List<CalculationRecord> getUserRecords(String userId, Integer limit);

    /**
     * 获取产品的计算记录
     */
    List<CalculationRecord> getProductRecords(Long productId);

    /**
     * 获取最近的计算记录
     */
    List<CalculationRecord> getLatestRecords(Integer limit);

    /**
     * 保存计算记录
     */
    boolean saveCalculationRecord(CalculationRecord record);
}