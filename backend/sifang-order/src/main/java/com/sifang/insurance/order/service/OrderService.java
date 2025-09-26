package com.sifang.insurance.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.order.entity.Order;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    Order createOrder(Order order);
    
    /**
     * 获取订单详情
     */
    Order getOrderById(Long id);
    
    /**
     * 根据订单号获取订单
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long id, Integer status);
    
    /**
     * 更新订单的核保状态
     */
    boolean updateUnderwritingStatus(Long id, Integer underwritingStatus);
    
    /**
     * 根据核保状态查询订单列表
     */
    List<Order> getOrdersByUnderwritingStatus(List<Integer> underwritingStatusList);
    
    /**
     * 用户支付订单
     */
    boolean payOrder(Long id, Integer payMethod, String tradeNo);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long id);
    
    /**
     * 分页查询订单列表
     */
    IPage<Order> pageOrders(IPage<Order> page, Long userId, Long productId, String orderNo, Integer status);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> getUserOrders(Long userId, Integer status);
    
    /**
     * 生成订单号
     */
    String generateOrderNo();
}