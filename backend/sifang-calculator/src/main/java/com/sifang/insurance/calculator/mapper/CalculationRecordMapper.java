package com.sifang.insurance.calculator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.calculator.entity.CalculationRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 计算记录Mapper接口
 */
public interface CalculationRecordMapper extends BaseMapper<CalculationRecord> {

    /**
     * 根据计算流水号查询记录
     */
    CalculationRecord selectByCalculateNo(@Param("calculateNo") String calculateNo);

    /**
     * 根据用户ID查询计算记录
     */
    List<CalculationRecord> selectByUserId(@Param("userId") String userId, 
                                          @Param("start") Integer start,
                                          @Param("limit") Integer limit);

    /**
     * 根据产品ID统计计算记录数
     */
    int countByProductId(@Param("productId") Long productId);

    /**
     * 查询今日计算记录数量
     */
    int countTodayRecords();
}