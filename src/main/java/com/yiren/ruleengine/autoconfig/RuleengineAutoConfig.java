package com.yiren.ruleengine.autoconfig;


import com.yiren.ruleengine.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleengineAutoConfig {

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }
}
