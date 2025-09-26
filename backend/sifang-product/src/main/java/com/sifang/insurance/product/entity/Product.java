package com.sifang.insurance.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保险产品实体类
 */
@Data
@TableName("ins_product")
public class Product {
    /**
     * 产品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品编码
     */
    private String code;

    /**
     * 产品类型：1-意外险，2-医疗险，3-重疾险，4-寿险，5-理财险
     */
    private Integer type;

    /**
     * 保险公司ID
     */
    private Long companyId;

    /**
     * 保险公司名称
     */
    private String companyName;

    /**
     * 最低保费
     */
    private BigDecimal minPremium;

    /**
     * 最高保额
     */
    private BigDecimal maxCoverage;

    /**
     * 投保年龄下限
     */
    private Integer minAge;

    /**
     * 投保年龄上限
     */
    private Integer maxAge;

    /**
     * 保障期限（年）
     */
    private Integer coveragePeriod;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 产品特点
     */
    private String features;

    /**
     * 保障责任
     */
    private String coverage;

    /**
     * 免责条款
     */
    private String exclusions;

    /**
     * 产品状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 主图URL
     */
    private String mainImage;

    /**
     * 详情图URL列表（JSON格式）
     */
    private String detailImages;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}