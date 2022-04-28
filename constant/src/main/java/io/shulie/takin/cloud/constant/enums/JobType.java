package io.shulie.takin.cloud.constant.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 任务类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@AllArgsConstructor
public enum JobType {
    /**
     * 常规模式
     */
    INITIAL(0, "常规模式"),
    /**
     * 调试模式
     */
    DEBUG(3, "调试模式"),
    /**
     * 巡检模式
     */
    PATROL(4, "巡检模式"),
    /**
     * 试跑模式
     */
    TRY(5, "试跑模式"),
    // 格式化用
    ;
    @Getter
    @JsonValue
    private final Integer code;
    @Getter
    private final String description;

    @Override
    public String toString() {return code + ":" + description;}
}
