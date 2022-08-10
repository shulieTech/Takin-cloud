package io.shulie.takin.cloud.constant.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 资源类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@AllArgsConstructor
public enum ResourceType {
    /**
     * 消除警告
     */
    NODE("node"),

    DRILLING("drilling");

    @Getter
    private final String name;
}
