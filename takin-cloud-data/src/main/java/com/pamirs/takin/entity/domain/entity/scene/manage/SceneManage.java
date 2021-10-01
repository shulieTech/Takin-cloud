package com.pamirs.takin.entity.domain.entity.scene.manage;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.ext.content.trace.ContextExt;

/**
 * @author -
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManage extends ContextExt {

    private Long id;

    private Long tenantId;

    private String sceneName;

    private Integer status;

    private Date lastPtTime;

    private Integer scriptType;

    private Integer type;

    private Integer isDeleted;

    private Date createTime;

    private String features;

    private String createName;

    private Date updateTime;

    private String updateName;

    private String ptConfig;
}
