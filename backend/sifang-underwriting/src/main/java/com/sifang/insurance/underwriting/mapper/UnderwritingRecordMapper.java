package com.sifang.insurance.underwriting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

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
    
    /**
     * 查询需要同步到订单服务的核保记录
     * @return 待同步的核保记录列表
     */
    List<UnderwritingRecord> selectNeedSyncRecords();
    
    /**
     * 标记核保记录为已同步
     * @param id 核保记录ID
     * @return 更新条数
     */
    int markAsSynced(@Param("id") Long id);
    
    /**
     * 分页查询核保记录
     * @param params 查询参数
     * @return 核保记录列表
     */
    List<UnderwritingRecord> selectPageByCondition(Map<String, Object> params);
    
    /**
     * 根据条件查询核保记录总数
     * @param params 查询参数
     * @return 记录总数
     */
    Long selectCountByCondition(Map<String, Object> params);
    
    /**
     * 查询核保统计信息
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> selectStatistics(Map<String, Object> params);
}