package com.yiren.ruleengine.excption;

/**
 * 规则引擎通用异常
 * @author tianjie
 */
public class RuleExcption extends RuntimeException {

    public RuleExcption(String message) {
        super(message);
    }
}
