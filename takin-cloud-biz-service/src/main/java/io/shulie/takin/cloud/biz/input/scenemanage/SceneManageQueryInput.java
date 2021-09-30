package io.shulie.takin.cloud.biz.input.scenemanage;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.ext.content.trace.PagingContextExt;

/**
 * 场景列表查询
 *
 * @author qianshui
 * @date 2020/4/17 下午2:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryInput extends PagingContextExt {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    /**
     * 场景ids
     */
    private List<Long> sceneIds;

    private String lastPtStartTime;

    private String lastPtEndTime;
}
