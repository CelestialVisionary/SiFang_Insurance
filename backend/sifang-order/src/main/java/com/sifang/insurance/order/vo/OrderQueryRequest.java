package com.sifang.insurance.order.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单查询请求参数
 */
@Data
public class OrderQueryRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成，4-已退款
     */
    private Integer status;

    /**
     * 支付状态：0-未支付，1-支付成功，2-支付失败，3-支付中
     */
    private Integer payStatus;

    /**
     * 核保状态：0-待核保，1-核保通过，2-核保拒绝，3-人工复核中
     */
    private Integer underwritingStatus;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 分页页码
     */
    private Integer pageNum = 1;

    /**
     * 分页大小
     */
    private Integer pageSize = 10;
}