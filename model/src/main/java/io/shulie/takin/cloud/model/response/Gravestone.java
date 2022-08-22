package io.shulie.takin.cloud.model.response;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * 墓志铭
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@AllArgsConstructor
public class Gravestone {
    /**
     * 时间
     */
    private long time;
    /**
     * 类型
     */
    private String type;
    /**
     * 内容
     */
    private String content;
}
