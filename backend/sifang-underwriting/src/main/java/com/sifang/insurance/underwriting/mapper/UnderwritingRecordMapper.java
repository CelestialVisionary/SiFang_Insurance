package com.sifang.insurance.underwriting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 核保记录Mapper接口
 */
@Mapper
public interface UnderwritingRecordMapper extends BaseMapper<UnderwritingRecord> {
    
    /**
     * 根据订单ID查询核保记录
     * @param orderId 订单ID
     * @return 核保记录
     */
    UnderwritingRecord selectByOrderId(String orderId);
    
    /**
     * 根据用户ID查询核保记录列表
     * @param userId 用户ID
     * @return 核保记录列表
     */
    List<UnderwritingRecord> selectByUserId(Long userId);
}