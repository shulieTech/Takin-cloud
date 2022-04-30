package io.shulie.takin.cloud.constant.enums;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 命令类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
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
    // 格式化用
    ;
    @Getter
    @JsonValue
    private final int value;

    CommandType(int value) {
        this.value = value;
    }
}
