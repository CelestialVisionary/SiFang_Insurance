package com.sifang.insurance.calculator.service.impl;

import com.sifang.insurance.calculator.entity.CalculationRule;
import com.sifang.insurance.calculator.exception.RuleEngineException;
import com.sifang.insurance.calculator.service.RuleEngineService;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则引擎服务实现类
 */
@Service
public class RuleEngineServiceImpl implements RuleEngineService {

    // 存储规则会话的缓存
    private final Map<String, KieSession> kieSessionCache = new ConcurrentHashMap<>();

    // Kie容器，用于加载规则
    private final KieContainer kieContainer;

    public RuleEngineServiceImpl() {
        // 初始化Kie容器
        KieServices kieServices = KieServices.Factory.get();
        ReleaseId releaseId = kieServices.newReleaseId("com.sifang.insurance", "calculator-rules", "1.0.0");
        kieContainer = kieServices.newKieContainer(releaseId);
    }

    @Override
    public Double executeRule(Long ruleId, String ruleCode, Map<String, Object> parameters) {
        try {
            // 生成会话ID
            String sessionKey = ruleId != null ? "rule_" + ruleId : "code_" + ruleCode;
            
            // 获取或创建Kie会话
            KieSession kieSession = getOrCreateKieSession(sessionKey, ruleId, ruleCode);
            
            // 设置参数到会话中
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                kieSession.setGlobal(entry.getKey(), entry.getValue());
            }
            
            // 创建结果对象
            Map<String, Double> result = new HashMap<>();
            kieSession.setGlobal("result", result);
            
            // 执行规则
            kieSession.fireAllRules();
            
            // 获取计算结果
            Double premium = result.getOrDefault("premium", 0.0);
            
            // 清理会话
            kieSession.dispose();
            
            return premium;
        } catch (Exception e) {
            throw new RuleEngineException(ruleId, "规则执行失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void loadRule(CalculationRule rule) {
        try {
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            
            // 构建Drools规则内容
            String ruleContent = buildDroolsRuleContent(rule);
            String rulePath = "src/main/resources/rules/" + rule.getRuleCode() + ".drl";
            
            kieFileSystem.write(rulePath, ruleContent);
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
            
            if (kieBuilder.getResults().hasMessages()) {
                throw new RuleEngineException(rule.getId(), "规则加载失败: " + kieBuilder.getResults().toString());
            }
            
            KieModule kieModule = kieBuilder.getKieModule();
            kieContainer.updateToVersion(kieModule.getReleaseId());
            
            // 清除缓存
            String sessionKey = "rule_" + rule.getId();
            kieSessionCache.remove(sessionKey);
            kieSessionCache.remove("code_" + rule.getRuleCode());
        } catch (Exception e) {
            throw new RuleEngineException(rule.getId(), "规则加载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void unloadRule(Long ruleId, String ruleCode) {
        // 从缓存中移除规则会话
        if (ruleId != null) {
            kieSessionCache.remove("rule_" + ruleId);
        }
        if (ruleCode != null) {
            kieSessionCache.remove("code_" + ruleCode);
        }
    }

    /**
     * 获取或创建Kie会话
     */
    private KieSession getOrCreateKieSession(String sessionKey, Long ruleId, String ruleCode) {
        return kieSessionCache.computeIfAbsent(sessionKey, key -> {
            // 如果规则代码不为空，使用规则代码作为会话名
            String sessionName = ruleCode != null ? ruleCode : "defaultRuleSession";
            return kieContainer.newKieSession(sessionName);
        });
    }

    /**
     * 构建Drools规则内容
     */
    private String buildDroolsRuleContent(CalculationRule rule) {
        // 这里简化实现，实际应用中需要根据规则内容动态生成Drools规则
        StringBuilder ruleContent = new StringBuilder();
        ruleContent.append("package com.sifang.insurance.rules\n\n");
        ruleContent.append("import java.util.Map\n\n");
        ruleContent.append("global Map result\n\n");
        ruleContent.append("rule \"").append(rule.getRuleName()).append("\"\n");
        ruleContent.append("    salience 10\n");
        ruleContent.append("when\n");
        ruleContent.append("    // 规则条件，根据实际业务逻辑设置\n");
        ruleContent.append("then\n");
        ruleContent.append("    // 根据规则内容执行计算\n");
        if (rule.getBaseRate() != null) {
            ruleContent.append("    Double premium = baseRate * amount;\n");
        }
        ruleContent.append("    result.put(\"premium\", premium);\n");
        ruleContent.append("end\n");
        return ruleContent.toString();
    }
}