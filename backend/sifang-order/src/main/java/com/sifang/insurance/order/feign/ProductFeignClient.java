package com.sifang.insurance.order.feign;

import com.sifang.insurance.common.response.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 产品服务Feign客户端
 */
@FeignClient(name = "sifang-product", path = "/api/product")
public interface ProductFeignClient {

    /**
     * 根据产品ID查询产品信息
     * 
     * @param productId 产品ID
     * @return 产品信息
     */
    @GetMapping("/get/{productId}")
    CommonResult<ProductDTO> getProductById(@PathVariable("productId") Long productId);

    /**
     * 产品DTO
     */
    class ProductDTO {
        private Long id;
        private String productName;
        private String productCode;
        private String description;
        private String category;
        private String subCategory;
        private Double minPremium;
        private Double maxPremium;
        private Integer minInsureAge;
        private Integer maxInsureAge;
        private Integer insurancePeriod;
        private String status;

        // getter and setter
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public Double getMinPremium() {
            return minPremium;
        }

        public void setMinPremium(Double minPremium) {
            this.minPremium = minPremium;
        }

        public Double getMaxPremium() {
            return maxPremium;
        }

        public void setMaxPremium(Double maxPremium) {
            this.maxPremium = maxPremium;
        }

        public Integer getMinInsureAge() {
            return minInsureAge;
        }

        public void setMinInsureAge(Integer minInsureAge) {
            this.minInsureAge = minInsureAge;
        }

        public Integer getMaxInsureAge() {
            return maxInsureAge;
        }

        public void setMaxInsureAge(Integer maxInsureAge) {
            this.maxInsureAge = maxInsureAge;
        }

        public Integer getInsurancePeriod() {
            return insurancePeriod;
        }

        public void setInsurancePeriod(Integer insurancePeriod) {
            this.insurancePeriod = insurancePeriod;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}