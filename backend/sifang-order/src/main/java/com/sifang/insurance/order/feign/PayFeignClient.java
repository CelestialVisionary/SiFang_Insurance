package com.sifang.insurance.order.feign;

import com.sifang.insurance.common.response.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 支付服务Feign客户端
 */
@FeignClient(name = "sifang-pay", path = "/api/pay")
public interface PayFeignClient {

    /**
     * 创建支付订单
     * 
     * @param orderNo 订单号
     * @param amount 支付金额
     * @param payMethod 支付方式
     * @param userId 用户ID
     * @return 支付链接
     */
    @PostMapping("/create")
    CommonResult<String> createPay(
            @RequestParam("orderNo") String orderNo,
            @RequestParam("amount") Double amount,
            @RequestParam("payMethod") Integer payMethod,
            @RequestParam("userId") Long userId);

    /**
     * 查询支付状态
     * 
     * @param orderNo 订单号
     * @return 支付状态信息
     */
    @PostMapping("/queryStatus")
    CommonResult<PayStatusDTO> queryPayStatus(@RequestParam("orderNo") String orderNo);

    /**
     * 支付状态DTO
     */
    class PayStatusDTO {
        private String orderNo;
        private Integer status; // 0-未支付，1-支付成功，2-支付失败，3-支付中
        private String statusDesc;
        private String payNo;
        private Double amount;
        private String payMethod;

        // getter and setter
        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getStatusDesc() {
            return statusDesc;
        }

        public void setStatusDesc(String statusDesc) {
            this.statusDesc = statusDesc;
        }

        public String getPayNo() {
            return payNo;
        }

        public void setPayNo(String payNo) {
            this.payNo = payNo;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getPayMethod() {
            return payMethod;
        }

        public void setPayMethod(String payMethod) {
            this.payMethod = payMethod;
        }
    }
}