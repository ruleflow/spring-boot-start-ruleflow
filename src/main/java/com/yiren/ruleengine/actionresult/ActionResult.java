package com.yiren.ruleengine.actionresult;


import com.yiren.ruleengine.anno.RuleResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 接受结果顶层接口
 * @author tianjie
 */
public interface ActionResult {
    /**
     * 获取结果类别
     * @return
     */
    default List<String> getResults(){
        List<String> actionResults = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields){
            RuleResult ruleResult = field.getAnnotation(RuleResult.class);
            if(null!=ruleResult){
                actionResults.add(ruleResult.value());
            }
        }

        return actionResults;
    }
}
