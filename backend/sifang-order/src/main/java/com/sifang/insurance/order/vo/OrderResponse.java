package com.sifang.insurance.order.vo;

import com.sifang.insurance.order.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应类
 */
@Data
public class OrderResponse {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 保费金额
     */
    private BigDecimal premiumAmount;

    /**
     * 保险金额
     */
    private BigDecimal insuranceAmount;

    /**
     * 保险期间（天）
     */
    private Integer insurancePeriod;

    /**
     * 保障开始时间
     */
    private LocalDateTime coverStartTime;

    /**
     * 保障结束时间
     */
    private LocalDateTime coverEndTime;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成，4-已退款
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 支付状态：0-未支付，1-支付成功，2-支付失败，3-支付中
     */
    private Integer payStatus;

    /**
     * 支付状态描述
     */
    private String payStatusDesc;

    /**
     * 核保状态：0-待核保，1-核保通过，2-核保拒绝，3-人工复核中
     */
    private Integer underwritingStatus;

    /**
     * 核保状态描述
     */
    private String underwritingStatusDesc;

    /**
     * 支付方式：1-支付宝，2-微信支付，3-银联支付
     */
    private Integer payMethod;

    /**
     * 支付方式描述
     */
    private String payMethodDesc;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付流水号
     */
    private String payNo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付链接（用于前端跳转支付）
     */
    private String payUrl;

    /**
     * 从订单实体转换为响应对象
     */
    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setProductId(order.getProductId());
        response.setProductName(order.getProductName());
        response.setPremiumAmount(order.getPremiumAmount());
        response.setInsuranceAmount(order.getInsuranceAmount());
        response.setInsurancePeriod(order.getInsurancePeriod());
        response.setCoverStartTime(order.getCoverStartTime());
        response.setCoverEndTime(order.getCoverEndTime());
        response.setStatus(order.getStatus());
        response.setStatusDesc(getStatusDesc(order.getStatus()));
        response.setPayStatus(order.getPayStatus());
        response.setPayStatusDesc(getPayStatusDesc(order.getPayStatus()));
        response.setUnderwritingStatus(order.getUnderwritingStatus());
        // underwritingStatusDesc会通过getter自动生成
        response.setPayMethod(order.getPayMethod());
        response.setPayMethodDesc(getPayMethodDesc(order.getPayMethod()));
        response.setPayTime(order.getPayTime());
        response.setPayNo(order.getPayNo());
        response.setCreateTime(order.getCreateTime());
        return response;
    }

    /**
     * 获取订单状态描述
     */
    private static String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已取消";
            case 3: return "已完成";
            case 4: return "已退款";
            default: return "未知状态";
        }
    }

    /**
     * 获取支付状态描述
     */
    private static String getPayStatusDesc(Integer payStatus) {
        switch (payStatus) {
            case 0: return "未支付";
            case 1: return "支付成功";
            case 2: return "支付失败";
            case 3: return "支付中";
            default: return "未知状态";
        }
    }

    /**
     * 获取核保状态描述
     */
    public String getUnderwritingStatusDesc() {
        if (underwritingStatus == null) {
            return "";
        }
        switch (underwritingStatus) {
            case 0: return "待核保";
            case 1: return "核保通过";
            case 2: return "核保拒绝";
            case 3: return "人工复核中";
            default: return "未知状态";
        }
    }

    /**
     * 获取支付方式描述
     */
    private static String getPayMethodDesc(Integer payMethod) {
        switch (payMethod) {
            case 1: return "支付宝";
            case 2: return "微信支付";
            case 3: return "银联支付";
            default: return "未知方式";
        }
    }
}