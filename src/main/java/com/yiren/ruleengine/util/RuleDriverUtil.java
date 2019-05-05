package com.yiren.ruleengine.util;

import com.yiren.ruleengine.excption.RuleExcption;
import com.yiren.ruleengine.rules.Rule;
import com.yiren.ruleengine.rules.RuleContainer;
import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.*;
import org.kie.internal.io.ResourceFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 负责执行规则
 * @author tianjie
 */
@Slf4j
public class RuleDriverUtil {

    private static final String rn = "\r\n";

    //执行规则
    public static void excute(RuleContainer ruleContainer){
        KieSession kSession = null;
        try {
            KnowledgeBuilderConfiguration configuration = new KnowledgeBuilderConfigurationImpl(RuleDriverUtil.class.getClassLoader());
            KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder(configuration);
            List<Rule> rules = ruleContainer.getRules();
            StringBuffer rule = new StringBuffer();
            appendRuleStr(ruleContainer, rules, rule);
            log.info("执行规则{}",rule.toString());
            kb.add(ResourceFactory.newByteArrayResource(rule.toString()
                    .getBytes("utf-8")), ResourceType.DRL);
            //错误的类型化集合。
            KnowledgeBuilderErrors errors = kb.getErrors();
            if(!errors.isEmpty()){
                StringBuffer errorMsg = new StringBuffer();
                for (KnowledgeBuilderError error : errors) {
                    errorMsg.append(error.getMessage());
                }
                throw new RuleExcption(errorMsg.toString());
            }
            KieBaseConfiguration kieBaseConfiguration = new RuleBaseConfiguration(RuleDriverUtil.class.getClassLoader());
            InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase(kieBaseConfiguration);
            kBase.addPackages(kb.getKnowledgePackages());
            kSession = kBase.newKieSession();
            kSession.insert(ruleContainer.getRuleKnowledge());
            kSession.setGlobal( "actionResult", ruleContainer.getActionResult() );
            //执行规则计算
            kSession.fireAllRules();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (kSession != null)
                kSession.dispose();
        }
    }

    /**
     * 拼接规则字符串
     * @param ruleContainer
     * @param rules
     * @param rule
     */
    private static void appendRuleStr(RuleContainer ruleContainer, List<Rule> rules, StringBuffer rule) {
        rule.append("package rules").append(rn);
        rule.append("import "+RuleContainer.class.getName()+";").append(rn);
        rule.append("import "+ruleContainer.getRuleKnowledge().getClass().getName()+";").append(rn);
        rule.append("import "+ruleContainer.getActionResult().getClass().getName()+";").append(rn);
        rule.append("global "+ruleContainer.getActionResult().getClass().getSimpleName()+" actionResult").append(rn);
        for(int i = 0; i<rules.size() ;i++){
            rule.append("rule \"rule"+i+"\"").append(rn);
            rule.append("when").append(rn);
            rule.append("supplier:"+ruleContainer.getRuleKnowledge().getClass().getSimpleName()+"("+rules.get(i).getCondition()+")").append(rn);
            rule.append("then").append(rn);
            rule.append(rules.get(i).getAction()).append(rn);
            rule.append("end").append(rn);
        }
    }

    /**
     * 测试规则是否可用
     * @param ruleContainer
     * @return
     */
    public boolean testRule(RuleContainer ruleContainer){
        try {
            KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
            List<Rule> rules = ruleContainer.getRules();
            StringBuffer rule = new StringBuffer();
            appendRuleStr(ruleContainer, rules, rule);
            kb.add(ResourceFactory.newByteArrayResource(rule.toString()
                    .getBytes("utf-8")), ResourceType.DRL);
            //错误的类型化集合。
            KnowledgeBuilderErrors errors = kb.getErrors();
            if(!errors.isEmpty()){
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
