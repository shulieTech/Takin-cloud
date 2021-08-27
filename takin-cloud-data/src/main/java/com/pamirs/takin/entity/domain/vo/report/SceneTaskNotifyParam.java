package com.pamirs.takin.entity.domain.vo.report;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-04-23
 */
@Data
public class SceneTaskNotifyParam {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 客户Id 新增
     */
    private Long customerId;

    /**
     * 状态
     */
    private String status;

    /**
     * 消息
     */
    private String msg;

}
