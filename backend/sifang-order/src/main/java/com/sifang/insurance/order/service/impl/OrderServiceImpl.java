package com.sifang.insurance.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.order.entity.Order;
import com.sifang.insurance.order.mapper.OrderMapper;
import com.sifang.insurance.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ORDER_NO_PREFIX = "ORDER";
    private static final String ORDER_NO_COUNTER_KEY = "order:no:counter:";
    private static final int ORDER_EXPIRE_HOURS = 24; // 订单24小时内有效

    @Override
    @Transactional
    public Order createOrder(Order order) {
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setOrderStatus(1); // 待支付状态
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        save(order);
        
        // 缓存订单信息
        cacheOrder(order);
        
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        // 先从缓存获取
        Order order = getOrderFromCache(id);
        if (order == null) {
            // 缓存未命中，从数据库查询
            order = orderMapper.selectById(id);
            if (order != null) {
                cacheOrder(order);
            }
        }
        return order;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long id, Integer status) {
        boolean result = orderMapper.updateOrderStatus(id, status) > 0;
        if (result) {
            // 清除缓存
            clearOrderCache(id);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean payOrder(Long id, Integer payMethod, String tradeNo) {
        Order order = getOrderById(id);
        if (order != null && order.getOrderStatus() == 1) { // 待支付状态
            order.setOrderStatus(2); // 已支付状态
            order.setPayMethod(payMethod);
            order.setPayTime(LocalDateTime.now());
            order.setPayTradeNo(tradeNo);
            order.setUpdateTime(LocalDateTime.now());
            boolean result = updateById(order);
            if (result) {
                // 清除缓存
                clearOrderCache(id);
            }
            return result;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order != null && order.getOrderStatus() == 1) { // 待支付状态
            // 检查订单是否已过期
            if (order.getCreateTime().plusHours(ORDER_EXPIRE_HOURS).isBefore(LocalDateTime.now())) {
                order.setOrderStatus(4); // 已取消状态
                order.setUpdateTime(LocalDateTime.now());
                boolean result = updateById(order);
                if (result) {
                    // 清除缓存
                    clearOrderCache(id);
                }
                return result;
            }
        }
        return false;
    }

    @Override
    public IPage<Order> pageOrders(IPage<Order> page, Long userId, Long productId, String orderNo, Integer status) {
        return orderMapper.selectPage(page, userId, productId, orderNo, status);
    }

    @Override
    public List<Order> getUserOrders(Long userId, Integer status) {
        return orderMapper.selectByUserIdAndStatus(userId, status);
    }

    @Override
    public String generateOrderNo() {
        // 格式：ORDER + 年月日时分 + 6位序号
        String dateStr = DateTimeFormatter.ofPattern("yyyyMMddHHmm").format(LocalDateTime.now());
        String counterKey = ORDER_NO_COUNTER_KEY + dateStr;
        
        // 使用Redis原子递增生成序号
        Long counter = redisTemplate.opsForValue().increment(counterKey, 1);
        
        // 设置计数器过期时间（到当天结束）
        redisTemplate.expireAt(counterKey, LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        
        // 格式化为6位序号
        String counterStr = String.format("%06d", counter);
        
        return ORDER_NO_PREFIX + dateStr + counterStr;
    }

    /**
     * 缓存订单信息
     */
    private void cacheOrder(Order order) {
        String cacheKey = "order:detail:" + order.getId();
        redisTemplate.opsForValue().set(cacheKey, order, 1, TimeUnit.HOURS);
    }

    /**
     * 从缓存获取订单信息
     */
    private Order getOrderFromCache(Long id) {
        String cacheKey = "order:detail:" + id;
        return (Order) redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 清除订单缓存
     */
    private void clearOrderCache(Long id) {
        String cacheKey = "order:detail:" + id;
        redisTemplate.delete(cacheKey);
    }
}