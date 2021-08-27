package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.bean.scenemanage
 * @description:
 * @date 2020/10/22 8:07 下午
 */
@Data
public class SceneManageQueryVO extends CloudUserCommonRequestExt implements Serializable {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    private Integer type;

}
