package io.shulie.takin.ext.content.enginecall;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-05-14
 */
@Data
public class ScheduleEventRequestExt implements Serializable {

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 场景任务ID
     */
    private Long taskId;

    /**
     * 客户Id
     */
    private Long customerId;

    /**
     * 扩展参数
     */
    private Map<String, Object> extend;
}
