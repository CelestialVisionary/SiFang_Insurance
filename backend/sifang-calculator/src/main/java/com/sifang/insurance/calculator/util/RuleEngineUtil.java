package com.sifang.insurance.calculator.util;

import com.sifang.insurance.calculator.exception.RuleEngineException;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 规则引擎工具类
 */
public class RuleEngineUtil {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineUtil.class);
    private static final Map<String, KieContainer> containerMap = new HashMap<>();
    private static final KieServices kieServices = KieServices.Factory.get();

    /**
     * 从规则文件加载规则引擎
     */
    public static KieContainer loadRulesFromFile(String ruleId, String ruleFilePath) {
        try {
            KieFileSystem kfs = kieServices.newKieFileSystem();
            kfs.write(ResourceFactory.newFileResource(new File(ruleFilePath)));
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
            
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                logger.error("规则编译错误: {}", results.getMessages());
                throw new RuleEngineException("规则编译失败", results.getMessages().toString());
            }
            
            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
            containerMap.put(ruleId, kieContainer);
            return kieContainer;
        } catch (Exception e) {
            logger.error("加载规则文件失败: {}", e.getMessage(), e);
            throw new RuleEngineException("加载规则文件失败", e);
        }
    }

    /**
     * 从规则内容加载规则引擎
     */
    public static KieContainer loadRulesFromString(String ruleId, String ruleContent) {
        try {
            KieFileSystem kfs = kieServices.newKieFileSystem();
            kfs.write("src/main/resources/rule.drl", ruleContent);
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
            
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                logger.error("规则编译错误: {}", results.getMessages());
                throw new RuleEngineException("规则编译失败", results.getMessages().toString());
            }
            
            KieModule kieModule = kieBuilder.getKieModule();
            KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
            containerMap.put(ruleId, kieContainer);
            return kieContainer;
        } catch (Exception e) {
            logger.error("加载规则内容失败: {}", e.getMessage(), e);
            throw new RuleEngineException("加载规则内容失败", e);
        }
    }

    /**
     * 从Excel决策表加载规则
     */
    public static KieContainer loadRulesFromExcel(String ruleId, InputStream excelStream) {
        try {
            SpreadsheetCompiler compiler = new SpreadsheetCompiler();
            String drl = compiler.compile(excelStream, InputType.XLS);
            logger.debug("Excel规则编译结果: {}", drl);
            return loadRulesFromString(ruleId, drl);
        } catch (Exception e) {
            logger.error("加载Excel规则失败: {}", e.getMessage(), e);
            throw new RuleEngineException("加载Excel规则失败", e);
        }
    }

    /**
     * 获取规则会话
     */
    public static KieSession getKieSession(String ruleId) {
        KieContainer kieContainer = containerMap.get(ruleId);
        if (kieContainer == null) {
            throw new RuleEngineException("规则引擎未初始化: " + ruleId);
        }
        return kieContainer.newKieSession();
    }

    /**
     * 执行规则
     */
    public static void executeRules(String ruleId, Object... facts) {
        KieSession kieSession = null;
        try {
            kieSession = getKieSession(ruleId);
            for (Object fact : facts) {
                kieSession.insert(fact);
            }
            kieSession.fireAllRules();
        } catch (Exception e) {
            logger.error("执行规则失败: {}", e.getMessage(), e);
            throw new RuleEngineException("执行规则失败", e);
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    /**
     * 刷新规则引擎
     */
    public static void refreshRule(String ruleId) {
        containerMap.remove(ruleId);
    }

    /**
     * 清除所有规则引擎缓存
     */
    public static void clearAll() {
        containerMap.clear();
    }
}