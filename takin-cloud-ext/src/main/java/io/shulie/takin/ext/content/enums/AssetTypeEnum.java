package io.shulie.takin.ext.content.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 压测报告、业务活动流量验证、脚本调试、巡检场景(暂时不做)
 *
 * @author caijianying
 */
@Getter
@AllArgsConstructor
public enum AssetTypeEnum {
    /**
     * 压测报告
     */
    PRESS_REPORT(1, "压测报告"),
    /**
     * 业务活动流量验证
     */
    ACTIVITY_CHECK(2, "业务活动流量验证"),
    /**
     * 脚本调试
     */
    SCRIPT_DEBUG(3, "脚本调试"),
    /**
     * 巡检场景
     */
    PATRO_SCENE(4, "巡检场景");

    private Integer code;
    private String name;
}
