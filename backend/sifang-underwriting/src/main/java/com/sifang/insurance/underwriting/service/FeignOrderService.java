package com.sifang.insurance.underwriting.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 订单服务Feign客户端
 * 用于调用订单服务的API接口
 */
@FeignClient(name = "sifang-order", path = "/api/order")
public interface FeignOrderService {

    /**
     * 更新订单的核保状态
     * @param data 核保状态数据
     * @return 是否更新成功
     */
    @PostMapping("/underwriting-status/update")
    boolean updateUnderwritingStatus(@RequestBody Map<String, Object> data);
}