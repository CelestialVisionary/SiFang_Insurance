package com.sifang.insurance.calculator.controller;

import com.sifang.insurance.calculator.dto.CalculateRequest;
import com.sifang.insurance.calculator.dto.CalculateResponse;
import com.sifang.insurance.calculator.entity.CalculationRecord;
import com.sifang.insurance.calculator.service.CalculationRecordService;
import com.sifang.insurance.calculator.service.PremiumCalculatorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 保费计算控制器
 */
@RestController
@RequestMapping("/api/calculation")
public class CalculationController {

    @Autowired
    private PremiumCalculatorService premiumCalculatorService;

    @Autowired
    private CalculationRecordService calculationRecordService;

    /**
     * 计算保费
     */
    @PostMapping("/calculate")
    public CalculateResponse calculate(@RequestBody CalculateRequest request) {
        CalculationRecord record;
        if (request.getRuleId() != null) {
            // 根据规则ID计算
            record = premiumCalculatorService.calculatePremiumByRule(request.getRuleId(), request.getUserId(), request.getParams());
        } else {
            // 根据产品ID计算
            record = premiumCalculatorService.calculatePremium(request.getProductId(), request.getUserId(), request.getParams());
        }
        return convertToResponse(record);
    }

    /**
     * 查询计算记录
     */
    @GetMapping("/record/{calculateNo}")
    public CalculateResponse getRecord(@PathVariable String calculateNo) {
        CalculationRecord record = calculationRecordService.getByCalculateNo(calculateNo);
        return record != null ? convertToResponse(record) : null;
    }

    /**
     * 获取用户的计算记录
     */
    @GetMapping("/records/user/{userId}")
    public List<CalculateResponse> getUserRecords(@PathVariable String userId, @RequestParam(defaultValue = "20") Integer limit) {
        List<CalculationRecord> records = calculationRecordService.getUserRecords(userId, limit);
        return records.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 获取产品的计算记录
     */
    @GetMapping("/records/product/{productId}")
    public List<CalculateResponse> getProductRecords(@PathVariable Long productId) {
        List<CalculationRecord> records = calculationRecordService.getProductRecords(productId);
        return records.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 获取最近的计算记录
     */
    @GetMapping("/records/latest")
    public List<CalculateResponse> getLatestRecords(@RequestParam(defaultValue = "50") Integer limit) {
        List<CalculationRecord> records = calculationRecordService.getLatestRecords(limit);
        return records.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 将计算记录转换为响应DTO
     */
    private CalculateResponse convertToResponse(CalculationRecord record) {
        CalculateResponse response = new CalculateResponse();
        BeanUtils.copyProperties(record, response);
        return response;
    }
}