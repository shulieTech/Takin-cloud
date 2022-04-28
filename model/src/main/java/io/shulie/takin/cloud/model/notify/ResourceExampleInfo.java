package io.shulie.takin.cloud.model.notify;

import java.util.HashMap;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务实例信息
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleInfo extends Basic<Long> {
    private HashMap<String, Object> info;
}
