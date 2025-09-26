package com.sifang.insurance.underwriting.service;

import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;

/**
 * 核保服务接口
 */
public interface UnderwritingService {
    
    /**
     * 提交核保申请
     * @param request 核保请求
     * @return 核保响应
     */
    UnderwritingResponse submitUnderwriting(UnderwritingRequest request);
    
    /**
     * 执行核保流程
     * @param record 核保记录
     * @return 核保结果
     */
    UnderwritingResponse processUnderwriting(UnderwritingRecord record);
    
    /**
     * 查询核保结果
     * @param orderId 订单ID
     * @return 核保记录
     */
    UnderwritingRecord queryUnderwritingResult(String orderId);
    
    /**
     * 人工复核核保
     * @param id 核保记录ID
     * @param status 复核状态
     * @param reason 复核原因
     * @param underwriter 复核人
     * @return 是否成功
     */
    boolean manualReview(Long id, Integer status, String reason, String underwriter);
}