package com.sifang.insurance.underwriting.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 核保结果响应DTO
 */
@Data
public class UnderwritingResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // 订单ID
    private String orderId;
    
    // 核保状态：0-待核保 1-核保通过 2-核保拒绝 3-人工复核中
    private Integer status;
    
    // 核保状态描述
    private String statusDesc;
    
    // 核保结果原因
    private String resultReason;
    
    // 核保时间
    private Date underwritingTime;
    
    // 是否需要人工复核
    private Boolean needManualReview;
    
    // 建议操作
    private String suggestedAction;
}