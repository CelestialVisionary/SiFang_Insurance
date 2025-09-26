package com.sifang.insurance.underwriting.vo;

import com.sifang.insurance.underwriting.entity.Underwriting;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 核保响应类
 */
@Data
public class UnderwritingResponse {

    /**
     * 核保记录ID
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
     * 核保状态：0-待核保，1-核保通过，2-核保拒绝，3-人工复核中
     */
    private Integer status;

    /**
     * 核保状态描述
     */
    private String statusDesc;

    /**
     * 核保结论
     */
    private String conclusion;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 核保人
     */
    private String underwriter;

    /**
     * 核保时间
     */
    private LocalDateTime underwritingTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 从核保实体转换为响应对象
     */
    public static UnderwritingResponse fromEntity(Underwriting underwriting) {
        UnderwritingResponse response = new UnderwritingResponse();
        response.setId(underwriting.getId());
        response.setOrderNo(underwriting.getOrderNo());
        response.setUserId(underwriting.getUserId());
        response.setProductId(underwriting.getProductId());
        response.setStatus(underwriting.getStatus());
        response.setStatusDesc(getStatusDesc(underwriting.getStatus()));
        response.setConclusion(underwriting.getConclusion());
        response.setRejectReason(underwriting.getRejectReason());
        response.setUnderwriter(underwriting.getUnderwriter());
        response.setUnderwritingTime(underwriting.getUnderwritingTime());
        response.setCreateTime(underwriting.getCreateTime());
        response.setUpdateTime(underwriting.getUpdateTime());
        return response;
    }

    /**
     * 获取核保状态描述
     */
    private static String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待核保";
            case 1: return "核保通过";
            case 2: return "核保拒绝";
            case 3: return "人工复核中";
            default: return "未知状态";
        }
    }
}