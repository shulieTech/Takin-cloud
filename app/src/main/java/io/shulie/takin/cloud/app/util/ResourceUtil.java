package io.shulie.takin.cloud.app.util;

import cn.hutool.core.util.NumberUtil;

/**
 * 资源工具
 *
 * <a href="https://kubernetes.io/zh/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes">K8s文档</a>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class ResourceUtil {
    private static final String CPU_UNIT_C = "C";
    private static final String CPU_UNIT_M = "m";
    private static final String MEMORY_UNIT_M = "M";
    private static final String MEMORY_UNIT_MI = "Mi";
    private static final String MEMORY_UNIT_G = "G";

    /**
     * 转换CPU
     *
     * @param cpu 入参
     * @return 可量化的数值
     */
    public static Double convertCpu(String cpu) {
        if (NumberUtil.isNumber(cpu)) {
            return NumberUtil.parseDouble(cpu);
        } else if (cpu.endsWith(CPU_UNIT_C)) {
            return convertCpu(cpu.substring(0, cpu.length() - (CPU_UNIT_C.length())));
        } else if (cpu.endsWith(CPU_UNIT_M)) {
            Double value = convertCpu(cpu.substring(0, cpu.length() - (CPU_UNIT_M.length())));
            if (value == null) {return null;}
            return value / 1000;
        } else {return null;}
    }

    /**
     * 转换内存
     *
     * @param memory 入参
     * @return 可量化的数值
     */
    public static Long convertMemory(String memory) {
        if (NumberUtil.isLong(memory)) {
            return NumberUtil.parseLong(memory);
        } else if (memory.endsWith(MEMORY_UNIT_MI)) {
            Long value = convertMemory(memory.substring(0, memory.length() - (MEMORY_UNIT_MI.length())));
            if (value == null) {return null;}
            return value * 1024 * 1024;
        } else if (memory.endsWith(MEMORY_UNIT_M)) {
            Long value = convertMemory(memory.substring(0, memory.length() - (MEMORY_UNIT_M.length())));
            if (value == null) {return null;}
            return value * 1000 * 1000;
        } else if (memory.endsWith(MEMORY_UNIT_G)) {
            Long value = convertMemory(memory.substring(0, memory.length() - (MEMORY_UNIT_G.length())) + "M");
            if (value == null) {return null;}
            return value * 1024;
        } else {return null;}
    }
}
