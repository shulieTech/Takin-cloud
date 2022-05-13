package io.shulie.takin.cloud.constant.enums;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

import cn.hutool.core.text.CharSequenceUtil;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 线程组类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@AllArgsConstructor
public enum ThreadGroupType {
    /**
     * 固定线程数
     */
    CONSTANT(100, "固定", 0, 1),
    /**
     * 线性增长
     */
    LINEAR_GROWTH(101, "线性递增", 0, 2),
    /**
     * 阶段增长
     */
    STAGE_GROWTH(102, "阶段递增", 0, 3),
    /**
     * TPS
     */
    TPS(200, "TPS模式", 1, 1),
    /**
     * TPS
     */
    DIY(300, "自定义", 2, 0),
    /**
     * 流量调试
     */
    FLOW_DEBUG(403, "流量调试", 3, 0),
    /**
     * 巡检模式
     */
    INSPECTION_MODE(404, "巡检模式", 4, 0),
    /**
     * 试跑模式
     */
    TRY_RUN(405, "试跑模式(脚本调试)", 5, 0),
    // 格式化用
    ;
    @Getter
    @JsonValue
    private final Integer code;
    @Getter
    private final String name;
    @Getter
    private final Integer type;
    @Getter
    private final Integer model;

    @Override
    public String toString() {return code + ":" + name + "(" + type + "," + model + ")";}

    private static final Map<Integer, ThreadGroupType> EXAMPLE_MAP = new HashMap<>(6);
    private static final Map<String, ThreadGroupType> TYPE_MODE_EXAMPLE_MAP = new HashMap<>(6);

    static {
        Arrays.stream(values()).forEach(t -> {
            EXAMPLE_MAP.put(t.getCode(), t);
            TYPE_MODE_EXAMPLE_MAP.put(CharSequenceUtil.format("{}_{}", t.getType(), t.getModel()), t);
        });
    }

    @JsonCreator
    public static ThreadGroupType of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }

    /**
     * 通过type和mode兑换枚举
     *
     * @param type -
     * @param mode -
     * @return -
     */
    @SuppressWarnings("unused")
    public static ThreadGroupType ofTypeMode(Integer type, Integer mode) {
        return TYPE_MODE_EXAMPLE_MAP.get(CharSequenceUtil.format("{}_{}", type, mode));
    }
}
