package io.shulie.takin.ext.content.enginecall;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-05-14
 */
@Data
public class ScheduleResponseExt implements Serializable {

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 状态描述
     */
    private String errorMgs;

    /**
     * 拓展配置
     */
    private Map<String, Object> extendMap;

}
