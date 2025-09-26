package com.sifang.insurance.underwriting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 核保记录实体类
 */
@Data
@TableName("t_underwriting")
public class Underwriting {

    /**
     * 核保记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 核保状态：0-待核保，1-核保通过，2-核保拒绝，3-人工复核中
     */
    private Integer status;

    /**
     * 核保结论
     */
    private String conclusion;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 核保人
     */
    private String underwriter;

    /**
     * 核保时间
     */
    private LocalDateTime underwritingTime;

    /**
     * 投保信息JSON
     */
    private String insuredInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}