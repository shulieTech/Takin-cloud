package com.pamirs.takin.entity.domain.entity.scene.manage;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SceneBusinessActivityRef extends SceneRef implements Serializable {

    private static final long serialVersionUID = -7316897002322891486L;

    private Long id;

    private Long businessActivityId;

    private String businessActivityName;

    private String applicationIds;

    private String bindRef;

    private Integer isDeleted;

    private Date createTime;

    private String createName;

    private Date updateTime;

    private String updateName;

    private String goalValue;

    /**
     * 是否包含压测头
     */
    private Boolean hasPT;
}
