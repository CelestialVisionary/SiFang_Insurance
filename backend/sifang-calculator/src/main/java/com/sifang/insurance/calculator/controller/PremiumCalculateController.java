package com.sifang.insurance.calculator.controller;

import com.sifang.insurance.calculator.dto.CalculatePremiumRequest;
import com.sifang.insurance.calculator.dto.CalculatePremiumResponse;
import com.sifang.insurance.calculator.service.PremiumCalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 保费计算控制器
 */
@RestController
@RequestMapping("/api/calculator")
public class PremiumCalculateController {

    @Autowired
    private PremiumCalculateService premiumCalculateService;

    /**
     * 计算保费
     */
    @PostMapping("/calculate")
    public CalculatePremiumResponse calculatePremium(@RequestBody CalculatePremiumRequest request) {
        return premiumCalculateService.calculatePremium(request);
    }

    /**
     * 验证保费计算参数
     */
    @PostMapping("/validate")
    public boolean validateRequest(@RequestBody CalculatePremiumRequest request) {
        return premiumCalculateService.validateRequest(request);
    }
}