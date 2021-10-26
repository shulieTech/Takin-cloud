package io.shulie.takin.ext.content.emus;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liyuanba
 * @Date: 2021/10/13 3:11 下午
 */
public enum NodeTypeEnum {
    /**
     * TestPlan
     */
    TEST_PLAN("TestPlan"),
    /**
     * 线程组
     */
    THREAD_GROUP("ThreadGroup"),
    /**
     * 逻辑控制器
     */
    CONTROLLER("Controller"),
    /**
     * 取样器
     */
    SAMPLER("SamplerProxy", "Sampler", "Sample"),
    ;

    @Getter
    private String[] suffixes;

    NodeTypeEnum(String... suffixes){
        this.suffixes = suffixes;
    }

    public static Map<String, NodeTypeEnum> pool = new HashMap<>();
    static {
        for (NodeTypeEnum e : NodeTypeEnum.values()) {
            for (String suffix : e.getSuffixes()) {
                if (pool.containsKey(suffix)) {
                    continue;
                }
                pool.put(suffix, e);
            }
        }
    }

    public static NodeTypeEnum value(String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return null;
        }
        for (String suffix : pool.keySet()) {
            if (nodeName.endsWith(suffix)) {
                return pool.get(suffix);
            }
        }
        return null;
    }
}
