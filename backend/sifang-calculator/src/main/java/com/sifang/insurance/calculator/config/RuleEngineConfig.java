package com.sifang.insurance.calculator.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 规则引擎配置类
 */
@Configuration
public class RuleEngineConfig {

    @Value("${drools.rules.path:classpath:rules/}")
    private String rulesPath;

    @Value("${drools.update.enabled:true}")
    private boolean updateEnabled;

    /**
     * 创建KieServices Bean
     */
    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    /**
     * 创建KieContainer Bean
     */
    @Bean
    public KieContainer kieContainer(KieServices kieServices) {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/"));
        
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }
}