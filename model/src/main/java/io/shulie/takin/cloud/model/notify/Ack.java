package io.shulie.takin.cloud.model.notify;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命令确认
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Ack extends Basic<Long> {
    private String content;
}
