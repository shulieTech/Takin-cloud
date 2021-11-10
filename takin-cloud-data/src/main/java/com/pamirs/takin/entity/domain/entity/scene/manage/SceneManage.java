package com.pamirs.takin.entity.domain.entity.scene.manage;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

@Data
public class SceneManage implements Serializable {

    private static final long serialVersionUID = -5922461634087976404L;

    private Long id;

    private Long customerId;

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
    /**
     * 脚本解析结果
     */
    private String scriptAnalysisResult;

    private Long deptId;
    /**
     * 用户id
     */
    private Long userId;
}
