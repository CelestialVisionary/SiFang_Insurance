package com.sifang.insurance.calculator.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * Drools规则引擎配置类
 */
@Configuration
public class DroolsConfig {

    @Value("${drools.rules.path:classpath*:/rules/**/*}")
    private String rulesPath;

    /**
     * 创建KieServices
     */
    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    /**
     * 创建KieContainer
     */
    @Bean
    public KieContainer kieContainer(KieServices kieServices) throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        // 加载规则文件
        for (Resource resource : getRuleFiles()) {
            String filePath = resource.getURI().toString();
            String fileName = filePath.substring(filePath.lastIndexOf("/"));
            kieFileSystem.write("src/main/resources/rules" + fileName, 
                ResourceFactory.newUrlResource(resource.getURI().toURL()));
        }
        
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        Results results = kieBuilder.getResults();
        
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Drools Build Errors: " + results.getMessages());
        }
        
        KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }

    /**
     * 创建KieSession
     */
    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        return kieContainer.newKieSession();
    }

    /**
     * 获取所有规则文件
     */
    private Resource[] getRuleFiles() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(rulesPath);
    }
}