package com.sifang.insurance.calculator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.calculator.entity.CalculationRecord;
import com.sifang.insurance.calculator.mapper.CalculationRecordMapper;
import com.sifang.insurance.calculator.service.CalculationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 计算记录服务实现类
 */
@Service
public class CalculationRecordServiceImpl extends ServiceImpl<CalculationRecordMapper, CalculationRecord> implements CalculationRecordService {

    @Autowired
    private CalculationRecordMapper calculationRecordMapper;

    @Override
    public CalculationRecord getByCalculateNo(String calculateNo) {
        return calculationRecordMapper.selectByCalculateNo(calculateNo);
    }

    @Override
    public List<CalculationRecord> getUserRecords(String userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        // 调用Mapper方法时传入3个参数，start设为0表示从第一条开始
        return calculationRecordMapper.selectByUserId(userId, 0, limit);
    }

    @Override
    public List<CalculationRecord> getProductRecords(Long productId) {
        // 使用MyBatis Plus的QueryWrapper来查询，避免调用不存在的方法
        return this.lambdaQuery().eq(CalculationRecord::getProductId, productId)
                .orderByDesc(CalculationRecord::getCreateTime)
                .list();
    }

    @Override
    public List<CalculationRecord> getLatestRecords(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50;
        }
        // 使用MyBatis Plus的QueryWrapper来查询，避免调用不存在的方法
        return this.lambdaQuery()
                .orderByDesc(CalculationRecord::getCreateTime)
                .last("LIMIT " + limit)
                .list();
    }

    @Override
    public boolean saveCalculationRecord(CalculationRecord record) {
        Date now = new Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        return this.save(record);
    }
}