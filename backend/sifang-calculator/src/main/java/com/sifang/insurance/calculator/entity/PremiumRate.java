package com.sifang.insurance.calculator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保费费率实体类
 */
@Data
@TableName("premium_rate")
public class PremiumRate {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 年龄段开始
     */
    private Integer ageStart;
    
    /**
     * 年龄段结束
     */
    private Integer ageEnd;
    
    /**
     * 性别 (1:男 2:女)
     */
    private Integer gender;
    
    /**
     * 基础费率
     */
    private BigDecimal baseRate;
    
    /**
     * 风险系数
     */
    private BigDecimal riskFactor;
    
    /**
     * 是否有效 (1:有效 0:无效)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}