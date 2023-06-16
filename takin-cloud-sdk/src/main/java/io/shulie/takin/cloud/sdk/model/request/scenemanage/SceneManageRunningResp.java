package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xjz@io.shulie
 * @date 2023/6/16
 * @desc 压测中压测场景列表响应
 */
@Data
public class SceneManageRunningResp implements Serializable {
    /**
     * 场景id
     */
    private Long id;
    /**
     * 压测时间
     */
    private Integer duration;
    /**
     * 最后施压时间
     */
    private Date lastPtTime;
}
