package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import lombok.Data;

/**
 * @Author: mubai
 * @Date: 2020-05-12 17:28
 * @Description:
 */

@Data
public class SceneScriptRefQueryParam implements Serializable {
    private static final long serialVersionUID = -4923279781262825503L;

    private Long sceneId;

    private String fileName;

}
