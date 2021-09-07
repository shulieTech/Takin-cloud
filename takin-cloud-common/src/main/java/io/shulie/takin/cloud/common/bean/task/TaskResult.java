package io.shulie.takin.cloud.common.bean.task;

import java.util.Map;

import io.shulie.takin.cloud.common.enums.scenemanage.TaskStatusEnum;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/4/20 下午2:41
 */
@Data
public class TaskResult {
    /**
     * 场景ID
     */
    private Long sceneId;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 客户 ID 新增
     */
    private Long customerId;
    /**
     * 压力节点 调度 任务状态
     */
    private TaskStatusEnum status;
    /**
     * 状态描述
     */
    private String msg;
    /**
     * 是否主动停止
     */
    private Boolean forceStop;
    /**
     * 是否主动启动
     */
    private Boolean forceStart;
    /**
     * 拓展配置
     */
    private Map<String, Object> extendMap;

    public TaskResult(Long sceneId, Long taskId, Long customerId) {
        this.sceneId = sceneId;
        this.taskId = taskId;
        this.customerId = customerId;
    }

    public TaskResult() {

    }
}
