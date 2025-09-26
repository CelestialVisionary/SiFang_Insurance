package com.sifang.insurance.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据用户ID和订单状态查询订单列表
     */
    List<Order> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 分页查询订单列表
     */
    IPage<Order> selectPage(IPage<Order> page, @Param("userId") Long userId, @Param("productId") Long productId, @Param("orderNo") String orderNo, @Param("status") Integer status);
    
    /**
     * 根据订单号查询订单
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新订单的核保状态
     */
    int updateUnderwritingStatus(@Param("id") Long id, @Param("underwritingStatus") Integer underwritingStatus);
    
    /**
     * 根据核保状态查询订单列表
     */
    List<Order> selectByUnderwritingStatusList(@Param("underwritingStatusList") List<Integer> underwritingStatusList);
}