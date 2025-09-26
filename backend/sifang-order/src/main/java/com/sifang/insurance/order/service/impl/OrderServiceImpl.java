package com.sifang.insurance.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.order.entity.Order;
import com.sifang.insurance.order.feign.UnderwritingFeignClient;
import com.sifang.insurance.order.mapper.OrderMapper;
import com.sifang.insurance.order.service.OrderService;
import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.common.entity.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UnderwritingFeignClient underwritingFeignClient;

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
        order.setUnderwritingStatus(0); // 待核保
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        save(order);
        
        // 缓存订单信息
        cacheOrder(order);
        
        // 调用核保服务进行核保
        submitUnderwritingRequest(order);
        
        return order;
    }
    
    /**
     * 提交核保请求
     */
    private void submitUnderwritingRequest(Order order) {
        try {
            UnderwritingRequest underwritingRequest = new UnderwritingRequest();
            underwritingRequest.setOrderId(order.getOrderNo());
            underwritingRequest.setUserId(order.getUserId());
            underwritingRequest.setProductId(order.getProductId());
            
            // 构建投保人信息（这里需要根据实际情况从其他服务或数据库获取）
            // 简化实现，实际项目中应该从用户服务获取详细信息
            java.util.Map<String, Object> applicantInfo = new java.util.HashMap<>();
            applicantInfo.put("userId", order.getUserId());
            applicantInfo.put("productId", order.getProductId());
            applicantInfo.put("premiumAmount", order.getPremiumAmount());
            applicantInfo.put("insuredAmount", order.getInsuredAmount());
            
            underwritingRequest.setApplicantInfo(applicantInfo);
            
            // 调用核保服务
            ResponseResult<UnderwritingResponse> result = underwritingFeignClient.submitUnderwriting(underwritingRequest);
            
            if (result != null && result.getCode() == 200) {
                UnderwritingResponse underwritingResponse = result.getData();
                logger.info("核保请求提交成功，订单号: {}, 核保状态: {}", order.getOrderNo(), underwritingResponse.getStatus());
                
                // 更新订单的核保状态（调用公共方法）
                this.updateUnderwritingStatus(order.getId(), underwritingResponse.getStatus());
                
                // 根据核保结果更新订单状态
                if (underwritingResponse.getStatus() == 2) { // 核保拒绝
                    updateOrderStatus(order.getId(), 4); // 已取消
                    logger.info("核保拒绝，订单已取消，订单号: {}", order.getOrderNo());
                }
            } else {
                logger.error("核保请求提交失败，订单号: {}", order.getOrderNo());
            }
        } catch (Exception e) {
            logger.error("提交核保请求异常，订单号: {}", order.getOrderNo(), e);
            // 核保服务调用失败，不影响订单创建，但记录日志以便后续处理
        }
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
    
    @Override
    @Transactional
    public boolean updateUnderwritingStatus(Long id, Integer underwritingStatus) {
        boolean result = orderMapper.updateUnderwritingStatus(id, underwritingStatus) > 0;
        if (result) {
            // 清除缓存
            clearOrderCache(id);
        }
        return result;
    }
    
    @Override
    public List<Order> getOrdersByUnderwritingStatus(List<Integer> underwritingStatusList) {
        return orderMapper.selectByUnderwritingStatusList(underwritingStatusList);
    }
}