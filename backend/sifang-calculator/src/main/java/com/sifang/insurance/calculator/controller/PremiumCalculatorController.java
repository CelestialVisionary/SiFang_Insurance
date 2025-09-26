package com.sifang.insurance.calculator.controller;

import com.sifang.insurance.calculator.dto.CalculateRequest;
import com.sifang.insurance.calculator.dto.CalculateResponse;
import com.sifang.insurance.calculator.entity.CalculationRecord;
import com.sifang.insurance.calculator.exception.CalculationException;
import com.sifang.insurance.calculator.service.CalculationRecordService;
import com.sifang.insurance.calculator.service.PremiumCalculatorService;
import com.sifang.insurance.calculator.util.ResponseUtil;
import com.sifang.insurance.common.entity.Result;
import com.sifang.insurance.common.entity.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保费计算控制器（核心服务）
 */
@RestController
@RequestMapping("/api/calculator")
public class PremiumCalculatorController {

    @Autowired
    private PremiumCalculatorService premiumCalculatorService;

    @Autowired
    private CalculationRecordService calculationRecordService;

    /**
     * 计算保费
     */
    @PostMapping("/calculate")
    public ResponseEntity<Result<CalculateResponse>> calculate(@RequestBody CalculateRequest request) {
        try {
            // 验证必要参数
            if (StringUtils.isEmpty(request.getUserId())) {
                throw new CalculationException("用户ID不能为空");
            }
            
            if (request.getRuleId() == null && request.getProductId() == null) {
                throw new CalculationException("规则ID或产品ID必须提供一个");
            }
            
            CalculationRecord record;
            if (request.getRuleId() != null) {
                // 使用指定规则计算
                record = premiumCalculatorService.calculatePremiumByRule(
                        request.getRuleId(),
                        request.getUserId(),
                        request.getParams()
                );
            } else {
                // 使用默认规则计算
                record = premiumCalculatorService.calculatePremium(
                        request.getProductId(),
                        request.getUserId(),
                        request.getParams()
                );
            }
            
            // 转换为响应对象
            CalculateResponse response = convertToResponse(record);
            return ResponseUtil.success(response);
        } catch (CalculationException e) {
            // 处理业务异常
            return (ResponseEntity<Result<CalculateResponse>>)(Object)ResponseUtil.fail(ResultCode.BUSINESS_ERROR, e.getMessage());
        } catch (Exception e) {
            // 处理系统异常
            return (ResponseEntity<Result<CalculateResponse>>)(Object)ResponseUtil.serverError("系统错误: " + e.getMessage());
        }
    }

    /**
     * 根据计算流水号查询结果
     */
    @GetMapping("/result/{calculateNo}")
    public ResponseEntity<Result<CalculateResponse>> getCalculateResult(@PathVariable("calculateNo") String calculateNo) {
        try {
            CalculationRecord record = calculationRecordService.getByCalculateNo(calculateNo);
            if (record == null) {
                return (ResponseEntity<Result<CalculateResponse>>)(Object)ResponseUtil.notFound("计算记录不存在");
            }
            
            CalculateResponse response = convertToResponse(record);
            return ResponseUtil.success(response);
        } catch (Exception e) {
            return (ResponseEntity<Result<CalculateResponse>>)(Object)ResponseUtil.fail(ResultCode.BUSINESS_ERROR, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的计算记录
     */
    @GetMapping("/records/user/{userId}")
    public ResponseEntity<Result<List<CalculateResponse>>> getUserRecords(
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<CalculationRecord> records = calculationRecordService.getUserRecords(userId, limit);
            List<CalculateResponse> responses = records.stream()
                    .map(this::convertToResponse)
                    .toList();
            return ResponseUtil.success(responses);
        } catch (Exception e) {
            return (ResponseEntity<Result<List<CalculateResponse>>>)(Object)ResponseUtil.fail(ResultCode.BUSINESS_ERROR, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近的计算记录
     */
    @GetMapping("/records/latest")
    public ResponseEntity<Result<List<CalculateResponse>>> getLatestRecords(@RequestParam(defaultValue = "50") Integer limit) {
        try {
            // 限制最大查询数量
            limit = Math.min(limit, 100);
            List<CalculationRecord> records = calculationRecordService.getLatestRecords(limit);
            List<CalculateResponse> responses = records.stream()
                    .map(this::convertToResponse)
                    .toList();
            return ResponseUtil.success(responses);
        } catch (Exception e) {
            return (ResponseEntity<Result<List<CalculateResponse>>>)(Object)ResponseUtil.fail(ResultCode.BUSINESS_ERROR, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Result<Map<String, String>>> healthCheck() {
        Map<String, String> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "premium-calculator");
        return ResponseUtil.success(data);
    }

    /**
     * 将计算记录转换为响应DTO
     */
    private CalculateResponse convertToResponse(CalculationRecord record) {
        CalculateResponse response = new CalculateResponse();
        response.setCalculateNo(record.getCalculateNo());
        response.setBasePremium(record.getBasePremium());
        response.setFinalPremium(record.getFinalPremium());
        response.setDiscountAmount(record.getDiscountAmount());
        response.setAdditionalFee(record.getAdditionalFee());
        response.setCalculateDescription(record.getCalculateDescription());
        response.setStatus(record.getStatus());
        response.setFailReason(record.getFailReason());
        response.setCreateTime(record.getCreateTime());
        response.setProductId(record.getProductId());
        response.setProductName(record.getProductName());
        response.setRuleId(record.getRuleId());
        return response;
    }
}