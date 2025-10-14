package com.sifang.insurance.underwriting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 核保规则版本实体类
 */
@Data
@TableName("underwriting_rule_version")
public class UnderwritingRuleVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 关联的规则ID
    private Long ruleId;
    
    // 规则版本号
    private Integer version;
    
    // 规则内容快照
    private String ruleContent;
    
    // 规则名称
    private String ruleName;
    
    // 规则描述
    private String ruleDescription;
    
    // 规则类型
    private Integer ruleType;
    
    // 适用产品ID
    private Long productId;
    
    // 规则优先级
    private Integer priority;
    
    // 版本说明
    private String versionRemark;
    
    // 创建人
    private String createBy;
    
    // 创建时间
    private Date createTime;
    
    // 是否当前版本
    private Boolean isCurrent;
}