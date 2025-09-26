package com.sifang.insurance.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sifang.insurance.order.dto.CreateOrderRequest;
import com.sifang.insurance.order.entity.Order;
import com.sifang.insurance.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        // 构建订单对象
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(request.getProductId());
        order.setProductName(request.getProductName());
        order.setPremiumAmount(request.getPremiumAmount());
        order.setInsuredAmount(request.getInsuredAmount());
        order.setCoveragePeriod(request.getCoveragePeriod());
        order.setEffectiveDate(request.getEffectiveDate());
        order.setExpiryDate(request.getExpiryDate());
        order.setRemark(request.getRemark());
        
        // 创建订单
        return orderService.createOrder(order);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{id}")
    public Order getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{id}")
    public boolean payOrder(@PathVariable Long id, @RequestParam Integer payMethod, @RequestParam String tradeNo) {
        return orderService.payOrder(id, payMethod, tradeNo);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    public boolean cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping("/page")
    public IPage<Order> pageOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status) {
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderService.pageOrders(page, userId, productId, orderNo, status);
    }

    /**
     * 获取当前用户的订单列表
     */
    @GetMapping("/my-orders")
    public List<Order> getMyOrders(@RequestParam(required = false) Integer status) {
        Long userId = getCurrentUserId();
        return orderService.getUserOrders(userId, status);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        // 从Spring Security上下文获取用户信息
        // 简化实现，实际应该从JWT令牌或认证上下文获取
        return 1L;
    }
}