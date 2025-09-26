package com.sifang.insurance.underwriting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 核保记录实体类
 */
@Data
@TableName("underwriting_record")
public class UnderwritingRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 订单ID
    private String orderId;
    
    // 用户ID
    private Long userId;
    
    // 产品ID
    private Long productId;
    
    // 投保人信息JSON
    private String applicantInfo;
    
    // 被保险人信息JSON
    private String insuredInfo;
    
    // 核保状态：0-待核保 1-核保通过 2-核保拒绝 3-人工复核中
    private Integer status;
    
    // 核保结果原因
    private String resultReason;
    
    // 核保时间
    private Date underwritingTime;
    
    // 核保人
    private String underwriter;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;
}