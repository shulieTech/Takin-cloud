package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author xr.l
 */
@Data
@TableName("")
public class TryRunSceneInfo {

    private Long id;
    private Long sceneId;
    private Long reportId;
    private Long customerId;
    private Integer taskStatus;
    private Integer uploadStatus;
    private Date createTime;
    private Date updateTime;
}
