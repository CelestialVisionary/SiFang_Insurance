-- 核保规则示例数据
INSERT INTO underwriting_rule (rule_name, rule_description, rule_type, rule_content, product_id, priority, status, create_by, update_by, create_time, update_time)
VALUES
-- 基本年龄限制规则
('基本年龄限制规则', '投保人年龄必须在18-65岁之间', 1, 
'package com.sifang.insurance.underwriting.rules;

import com.sifang.insurance.underwriting.service.impl.UnderwritingServiceImpl.UnderwritingContext;

rule "基本年龄限制规则"
    when
        context: UnderwritingContext()
        applicantInfo: Map() from context.getApplicantInfo()
        age: Integer(intValue() < 18 || intValue() > 65) from applicantInfo.get("age")
    then
        context.setPass(false);
        context.setReason("投保人年龄必须在18-65岁之间");
end', 1, 100, 1, 'system', 'system', NOW(), NOW()),

-- 健康告知规则
('健康告知规则', '投保人必须无重大疾病史', 1,
'package com.sifang.insurance.underwriting.rules;

import com.sifang.insurance.underwriting.service.impl.UnderwritingServiceImpl.UnderwritingContext;
import java.util.Map;

rule "健康告知规则"
    when
        context: UnderwritingContext()
        applicantInfo: Map() from context.getApplicantInfo()
        healthInfo: Map() from applicantInfo.get("healthInfo")
        hasSeriousDisease: Boolean(booleanValue() == true) from healthInfo.get("hasSeriousDisease")
    then
        context.setPass(false);
        context.setManualReview(true);
        context.setReason("投保人有重大疾病史，需要人工复核");
end', 1, 200, 1, 'system', 'system', NOW(), NOW()),

-- 职业风险规则
('职业风险规则', '高危职业需要人工复核', 2,
'package com.sifang.insurance.underwriting.rules;

import com.sifang.insurance.underwriting.service.impl.UnderwritingServiceImpl.UnderwritingContext;
import java.util.Map;

rule "职业风险规则"
    when
        context: UnderwritingContext()
        applicantInfo: Map() from context.getApplicantInfo()
        occupation: String() from applicantInfo.get("occupation")
        eval(occupation != null && (occupation.contains("高空作业") || 
             occupation.contains("建筑工人") || 
             occupation.contains("矿工") || 
             occupation.contains("飞行员")))
    then
        context.setManualReview(true);
        context.setReason("投保人从事高危职业，需要人工复核");
end', 1, 300, 1, 'system', 'system', NOW(), NOW()),

-- 被保险人年龄规则
('被保险人年龄规则', '被保险人年龄必须在0-80岁之间', 1,
'package com.sifang.insurance.underwriting.rules;

import com.sifang.insurance.underwriting.service.impl.UnderwritingServiceImpl.UnderwritingContext;
import java.util.Map;

rule "被保险人年龄规则"
    when
        context: UnderwritingContext()
        insuredInfo: Map() from context.getInsuredInfo()
        age: Integer(intValue() < 0 || intValue() > 80) from insuredInfo.get("age")
    then
        context.setPass(false);
        context.setReason("被保险人年龄必须在0-80岁之间");
end', 1, 400, 1, 'system', 'system', NOW(), NOW());

-- 核保记录表结构（如果不存在）
CREATE TABLE IF NOT EXISTS underwriting_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    applicant_info TEXT COMMENT '投保人信息JSON',
    insured_info TEXT COMMENT '被保险人信息JSON',
    status INT NOT NULL DEFAULT 0 COMMENT '核保状态：0-待核保 1-核保通过 2-核保拒绝 3-人工复核中',
    result_reason VARCHAR(500) COMMENT '核保结果原因',
    underwriting_time DATETIME COMMENT '核保时间',
    underwriter VARCHAR(50) COMMENT '核保人',
    sync_status INT NOT NULL DEFAULT 0 COMMENT '同步状态：0-待同步 1-已同步',
    sync_time DATETIME COMMENT '同步时间',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核保记录表';

-- 核保规则表结构（如果不存在）
CREATE TABLE IF NOT EXISTS underwriting_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_description VARCHAR(255) COMMENT '规则描述',
    rule_type INT NOT NULL COMMENT '规则类型：1-基本规则 2-高级规则 3-特殊规则',
    rule_content TEXT NOT NULL COMMENT '规则内容（Drools规则）',
    product_id BIGINT NOT NULL COMMENT '适用产品ID',
    priority INT NOT NULL DEFAULT 100 COMMENT '规则优先级',
    status INT NOT NULL DEFAULT 1 COMMENT '规则状态：0-禁用 1-启用',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    KEY idx_product_id_status (product_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核保规则表';