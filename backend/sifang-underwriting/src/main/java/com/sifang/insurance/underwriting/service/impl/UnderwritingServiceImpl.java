package com.sifang.insurance.underwriting.service.impl;

import com.alibaba.fastjson.JSON;
import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.entity.UnderwritingRecord;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.mapper.UnderwritingRecordMapper;
import com.sifang.insurance.underwriting.service.UnderwritingRuleService;
import com.sifang.insurance.underwriting.service.UnderwritingService;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 核保服务实现类
 */
@Service
public class UnderwritingServiceImpl implements UnderwritingService {

    @Autowired
    private UnderwritingRecordMapper underwritingRecordMapper;
    
    @Autowired
    private UnderwritingRuleService underwritingRuleService;

    @Override
    @Transactional
    public UnderwritingResponse submitUnderwriting(UnderwritingRequest request) {
        // 创建核保记录
        UnderwritingRecord record = new UnderwritingRecord();
        record.setOrderId(request.getOrderId());
        record.setUserId(request.getUserId());
        record.setProductId(request.getProductId());
        record.setApplicantInfo(JSON.toJSONString(request.getApplicantInfo()));
        record.setInsuredInfo(request.getInsuredInfo() != null ? JSON.toJSONString(request.getInsuredInfo()) : null);
        record.setStatus(0); // 待核保
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        underwritingRecordMapper.insert(record);
        
        // 执行核保流程
        return processUnderwriting(record);
    }

    @Override
    @Transactional
    public UnderwritingResponse processUnderwriting(UnderwritingRecord record) {
        UnderwritingResponse response = new UnderwritingResponse();
        response.setOrderId(record.getOrderId());
        
        try {
            // 获取产品对应的核保规则
            List<UnderwritingRule> rules = underwritingRuleService.getEnabledRulesByProductId(record.getProductId());
            
            if (rules.isEmpty()) {
                // 无规则时默认通过
                record.setStatus(1); // 核保通过
                record.setResultReason("无核保规则，默认通过");
                response.setStatus(1);
                response.setStatusDesc("核保通过");
                response.setResultReason("无核保规则，默认通过");
                response.setNeedManualReview(false);
            } else {
                // 使用Drools规则引擎进行核保评估
                KieServices kieServices = KieServices.Factory.get();
                KieFileSystem kfs = kieServices.newKieFileSystem();
                
                // 添加规则
                for (UnderwritingRule rule : rules) {
                    kfs.write("src/main/resources/rules/" + rule.getId() + ".drl", 
                            ResourceFactory.newByteArrayResource(rule.getRuleContent().getBytes("UTF-8")));
                }
                
                KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
                if (kieBuilder.getResults().hasMessages()) {
                    throw new RuntimeException("规则编译失败: " + kieBuilder.getResults().toString());
                }
                
                KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
                KieSession kieSession = kieContainer.newKieSession();
                
                // 设置核保上下文对象（这里简化处理，实际应该创建专门的上下文类）
                UnderwritingContext context = new UnderwritingContext();
                context.setApplicantInfo(JSON.parseObject(record.getApplicantInfo()));
                if (record.getInsuredInfo() != null) {
                    context.setInsuredInfo(JSON.parseObject(record.getInsuredInfo()));
                }
                
                kieSession.insert(context);
                kieSession.fireAllRules();
                kieSession.dispose();
                
                // 根据上下文结果设置核保状态
                if (context.isPass()) {
                    record.setStatus(1); // 核保通过
                    response.setStatus(1);
                    response.setStatusDesc("核保通过");
                    response.setNeedManualReview(false);
                } else if (context.isManualReview()) {
                    record.setStatus(3); // 人工复核中
                    response.setStatus(3);
                    response.setStatusDesc("人工复核中");
                    response.setNeedManualReview(true);
                } else {
                    record.setStatus(2); // 核保拒绝
                    response.setStatus(2);
                    response.setStatusDesc("核保拒绝");
                    response.setNeedManualReview(false);
                }
                
                record.setResultReason(context.getReason());
                response.setResultReason(context.getReason());
            }
            
            // 更新核保记录
            record.setUnderwritingTime(new Date());
            record.setUpdateTime(new Date());
            underwritingRecordMapper.updateById(record);
            
            response.setUnderwritingTime(record.getUnderwritingTime());
            response.setSuggestedAction(getSuggestedAction(response.getStatus()));
            
        } catch (Exception e) {
            // 核保过程出错，标记为需要人工复核
            record.setStatus(3);
            record.setResultReason("系统处理异常: " + e.getMessage());
            record.setUpdateTime(new Date());
            underwritingRecordMapper.updateById(record);
            
            response.setStatus(3);
            response.setStatusDesc("人工复核中");
            response.setResultReason("系统处理异常，请等待人工复核");
            response.setNeedManualReview(true);
            response.setSuggestedAction("请联系客服进行人工复核");
        }
        
        return response;
    }

    @Override
    public UnderwritingRecord queryUnderwritingResult(String orderId) {
        return underwritingRecordMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional
    public boolean manualReview(Long id, Integer status, String reason, String underwriter) {
        UnderwritingRecord record = underwritingRecordMapper.selectById(id);
        if (record == null) {
            return false;
        }
        
        record.setStatus(status);
        record.setResultReason(reason);
        record.setUnderwriter(underwriter);
        record.setUnderwritingTime(new Date());
        record.setUpdateTime(new Date());
        
        return underwritingRecordMapper.updateById(record) > 0;
    }
    
    /**
     * 根据核保状态获取建议操作
     */
    private String getSuggestedAction(Integer status) {
        switch (status) {
            case 1: // 核保通过
                return "订单已核保通过，可继续后续流程";
            case 2: // 核保拒绝
                return "订单核保未通过，请联系客服了解详情";
            case 3: // 人工复核中
                return "订单正在人工复核中，请耐心等待";
            default:
                return "核保处理中，请稍后查询结果";
        }
    }
    
    /**
     * 核保上下文类，用于规则引擎评估
     */
    public static class UnderwritingContext {
        private boolean pass = true; // 默认通过
        private boolean manualReview = false; // 是否需要人工复核
        private String reason = "";
        private Object applicantInfo;
        private Object insuredInfo;
        
        // getter和setter方法
        public boolean isPass() {
            return pass;
        }
        public void setPass(boolean pass) {
            this.pass = pass;
        }
        public boolean isManualReview() {
            return manualReview;
        }
        public void setManualReview(boolean manualReview) {
            this.manualReview = manualReview;
        }
        public String getReason() {
            return reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
        public Object getApplicantInfo() {
            return applicantInfo;
        }
        public void setApplicantInfo(Object applicantInfo) {
            this.applicantInfo = applicantInfo;
        }
        public Object getInsuredInfo() {
            return insuredInfo;
        }
        public void setInsuredInfo(Object insuredInfo) {
            this.insuredInfo = insuredInfo;
        }
    }
}