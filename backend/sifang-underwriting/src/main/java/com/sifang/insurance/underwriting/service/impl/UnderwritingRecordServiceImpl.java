package com.sifang.insurance.underwriting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.mapper.UnderwritingRecordMapper;
import com.sifang.insurance.underwriting.service.UnderwritingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核保记录服务实现类
 */
@Service
public class UnderwritingRecordServiceImpl extends ServiceImpl<UnderwritingRecordMapper, UnderwritingRecord> implements UnderwritingRecordService {

    @Autowired
    private UnderwritingRecordMapper underwritingRecordMapper;

    @Override
    public Map<String, Object> pageQuery(Map<String, Object> params) {
        // 计算分页参数
        Integer page = (Integer) params.get("page");
        Integer size = (Integer) params.get("size");
        Integer offset = (page - 1) * size;
        
        params.put("offset", offset);
        params.put("limit", size);
        
        // 查询数据
        List<UnderwritingRecord> records = underwritingRecordMapper.selectPageByCondition(params);
        Long total = underwritingRecordMapper.selectCountByCondition(params);
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("size", size);
        result.put("current", page);
        result.put("pages", total % size == 0 ? total / size : total / size + 1);
        
        return result;
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        // 查询统计数据
        Map<String, Object> statistics = underwritingRecordMapper.selectStatistics(params);
        
        // 确保所有状态都有值
        if (statistics == null) {
            statistics = new HashMap<>();
        }
        
        // 计算总计
        Long totalCount = underwritingRecordMapper.selectCountByCondition(params);
        statistics.put("totalCount", totalCount);
        
        // 计算通过率
        Long passedCount = (Long) statistics.getOrDefault("passedCount", 0);
        double passRate = totalCount > 0 ? (double) passedCount / totalCount * 100 : 0;
        statistics.put("passRate", String.format("%.2f%%", passRate));
        
        return statistics;
    }

    @Override
    public UnderwritingRecord selectByOrderId(String orderId) {
        return underwritingRecordMapper.selectByOrderId(orderId);
    }

    @Override
    public boolean updateSyncStatus(Long id, Integer syncStatus) {
        UnderwritingRecord record = new UnderwritingRecord();
        record.setId(id);
        record.setSyncStatus(syncStatus);
        if (syncStatus == 1) {
            record.setSyncTime(new java.util.Date());
        }
        return this.updateById(record);
    }
}