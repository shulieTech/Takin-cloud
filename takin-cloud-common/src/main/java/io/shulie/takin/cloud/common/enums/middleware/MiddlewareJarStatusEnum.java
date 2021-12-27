package io.shulie.takin.cloud.common.enums.middleware;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 中间件支持状态枚举
 *
 * @author liuchuan
 * @date 2021/6/1 2:26 下午
 */
@Getter
@AllArgsConstructor
public enum MiddlewareJarStatusEnum {

    /**
     * 无需支持
     */
    NO_REQUIRED(3, "无需支持"),
    /**
     * 待验证
     */
    TO_BE_VERIFIED(4, "待验证"),
    /**
     * 待支持
     */
    TO_BE_SUPPORTED(2, "待支持"),
    /**
     * 已支持
     */
    SUPPORTED(1, "已支持");

    private final Integer code;
    private final String description;

    private static final Map<Integer, MiddlewareJarStatusEnum> CODE_INSTANCES = new HashMap<>(4);
    private static final Map<String, MiddlewareJarStatusEnum> DESCRIPTION_INSTANCES = new HashMap<>(4);

    static {
        for (MiddlewareJarStatusEnum e : MiddlewareJarStatusEnum.values()) {
            CODE_INSTANCES.put(e.getCode(), e);
            DESCRIPTION_INSTANCES.put(e.getDescription(), e);
        }
    }

    /**
     * 通过 desc 获得枚举
     *
     * @param description 描述
     * @return 枚举
     */
    public static MiddlewareJarStatusEnum of(String description) {
        return DESCRIPTION_INSTANCES.get(description);
    }

    /**
     * 通过 code 获得枚举
     *
     * @param code 状态
     * @return 枚举
     */
    public static MiddlewareJarStatusEnum of(Integer code) {
        return CODE_INSTANCES.get(code);
    }

}
