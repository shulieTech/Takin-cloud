package io.shulie.takin.cloud.constant.enums;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 命令类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Getter
@AllArgsConstructor
public enum CommandType {
    /**
     * 锁定资源
     */
    GRASP_RESOURCE(101),
    /**
     * 释放资源
     */
    RELEASE_RESOURCE(102),
    /**
     * 启动应用程序
     */
    START_APPLICATION(201),
    /**
     * 停止应用程序
     */
    STOP_APPLICATION(202),
    /**
     * 修改指标配置
     */
    MODIFY_METRICS_CONFIG(301),
    /**
     * 修改线程配置
     */
    MODIFY_THREAD_CONFIG(302),
    /**
     * 下发文件
     */
    ANNOUNCE_FILE(400),
    /**
     * 脚本校验
     */
    ANNOUNCE_SCRIPT(500),
    // 格式化用
    ;

    @JsonValue
    private final int value;

    private static final Map<Integer, CommandType> EXAMPLE_MAP = new HashMap<>(6);

    static {
        Arrays.stream(values()).forEach(t -> EXAMPLE_MAP.put(t.getValue(), t));
    }

    @JsonCreator
    public static CommandType of(Integer code) {
        return EXAMPLE_MAP.get(code);
    }

}
