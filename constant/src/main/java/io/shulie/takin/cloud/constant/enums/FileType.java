package io.shulie.takin.cloud.constant.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@AllArgsConstructor
public enum FileType {
    /**
     * 常规模式
     */
    SCRIPT(0, "脚本文件"),
    /**
     * 调试模式
     */
    DATA(1, "数据文件"),
    /**
     * 巡检模式
     */
    ATTACHMENT(2, "附件")
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
