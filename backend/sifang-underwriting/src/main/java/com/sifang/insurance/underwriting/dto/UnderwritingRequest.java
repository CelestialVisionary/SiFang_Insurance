package com.sifang.insurance.underwriting.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 核保申请请求DTO
 */
@Data
public class UnderwritingRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // 订单ID
    @NotNull(message = "订单ID不能为空")
    private String orderId;
    
    // 用户ID
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    // 产品ID
    @NotNull(message = "产品ID不能为空")
    private Long productId;
    
    // 投保人信息
    @NotNull(message = "投保人信息不能为空")
    private Map<String, Object> applicantInfo;
    
    // 被保险人信息
    private Map<String, Object> insuredInfo;
    
    // 其他核保所需信息
    private Map<String, Object> otherInfo;
}