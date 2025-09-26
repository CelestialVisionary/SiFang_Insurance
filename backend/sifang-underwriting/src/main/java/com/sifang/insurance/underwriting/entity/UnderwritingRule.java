package com.sifang.insurance.underwriting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 核保规则实体类
 */
@Data
@TableName("underwriting_rule")
public class UnderwritingRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 规则名称
    private String ruleName;
    
    // 规则描述
    private String ruleDescription;
    
    // 规则类型：1-基本规则 2-高级规则 3-特殊规则
    private Integer ruleType;
    
    // 规则内容（Drools规则）
    private String ruleContent;
    
    // 适用产品ID
    private Long productId;
    
    // 规则优先级
    private Integer priority;
    
    // 规则状态：0-禁用 1-启用
    private Integer status;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;
    
    // 创建人
    private String createBy;
    
    // 更新人
    private String updateBy;
}