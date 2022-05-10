package io.shulie.takin.cloud.app.util;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.util.NumberUtil;

/**
 * 资源工具
 *
 * <a href="https://kubernetes.io/zh/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes">K8s文档</a>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
public class ResourceUtil {
    private static final String CPU_UNIT_C = "C";
    private static final String CPU_UNIT_M = "m";

    private static final char MEMORY_UNIT_M = 'm';
    private static final String MEMORY_UNIT = "EPTGMk";
    private static final long[] MEMORY_NOT_I = new long[] {
        1000L,
        1000L * 1000L,
        1000L * 1000L * 1000L,
        1000L * 1000L * 1000L * 1000L,
        1000L * 1000L * 1000L * 1000L * 1000L,
        1000L * 1000L * 1000L * 1000L * 1000L * 1000L};
    private static final long[] MEMORY_I = new long[] {
        1024L,
        1024L * 1024L,
        1024L * 1024L * 1024L,
        1024L * 1024L * 1024L * 1024L,
        1024L * 1024L * 1024L * 1024L * 1024L,
        1024L * 1024L * 1024L * 1024L * 1024L * 1024L};

    /**
     * 转换CPU
     *
     * @param cpu 入参
     * @return 可量化的数值
     */
    public static Double convertCpu(String cpu) {
        if (cpu.endsWith(CPU_UNIT_C)) {
            return parseDouble(cpu.substring(0, cpu.length() - (CPU_UNIT_C.length())));
        } else if (cpu.endsWith(CPU_UNIT_M)) {
            Double value = parseDouble(cpu.substring(0, cpu.length() - (CPU_UNIT_M.length())));
            if (value == null) {return null;}
            return value / 1000;
        } else {return parseDouble(cpu);}
    }

    /**
     * 转换内存
     *
     * @param memory 入参
     * @return 可量化的数值
     */
    public static Long convertMemory(String memory) {
        // 不带单位直接返回
        if (NumberUtil.isNumber(memory)) {return NumberUtil.parseNumber(memory).longValue();}
        char[] memoryCharArray = memory.toCharArray();
        // 处理特殊的m单位
        if (memoryCharArray[memory.length() - 1] == MEMORY_UNIT_M) {
            String memoryRemoveM = memory.substring(0, memory.length() - 4);
            if (NumberUtil.isNumber(memoryRemoveM)) {return NumberUtil.parseNumber(memoryRemoveM).longValue();}
        }
        // 判断是使用1000递乘还是1024
        boolean useXi = memoryCharArray[memory.length() - 1] == 'i';
        // 得到乘数列表
        long[] multiplierArray = useXi ? MEMORY_I : MEMORY_NOT_I;
        // 得到单位所在的索引
        int unitIndex = memory.length() - (useXi ? 2 : 1);
        // 得到单位
        char memoryUnit = memoryCharArray[unitIndex];
        // 得到数值
        String memoryNumber = memory.substring(0, unitIndex);
        //得到乘数
        long multiplier = 0;
        int index = MEMORY_UNIT.indexOf(memoryUnit);
        if (index != -1) {
            // 单位索引倒序转换
            index = MEMORY_UNIT.length() - 1 - index;
            multiplier = multiplierArray[index];
        }
        if (multiplier == 0) {return null;}
        // 换算结果
        Long memoryValue = parseLong(memoryNumber);
        if (memoryValue != null) {return memoryValue * multiplier;}
        return null;
    }

    private static Double parseDouble(String value) {
        if (NumberUtil.isNumber(value)) {return NumberUtil.parseDouble(value);} else {return null;}
    }

    private static Long parseLong(String value) {
        if (NumberUtil.isLong(value)) {return NumberUtil.parseLong(value);} else {return null;}
    }

    /**
     * 进行精度测试
     * <p>根据<a href="https://kubernetes.io/zh/docs/concepts/configuration/manage-resources-containers/#meaning-of-memory">官网描述</a>,以下值的转换应该近似</p>
     *
     * <ul>
     *     <li>128974848</li>
     *     <li>129e6</li>
     *     <li>129M</li>
     *     <li>128974848000m</li>
     *     <li>123Mi</li>
     * </ul>
     *
     * @param args null
     */
    public static void main(String[] args) {
        log.info("{}", convertMemory("128974848"));
        log.info("{}", convertMemory("129E6"));
        log.info("{}", convertMemory("129M"));
        log.info("{}", convertMemory("128974848000m"));
        log.info("{}", convertMemory("123Mi"));
    }
}
