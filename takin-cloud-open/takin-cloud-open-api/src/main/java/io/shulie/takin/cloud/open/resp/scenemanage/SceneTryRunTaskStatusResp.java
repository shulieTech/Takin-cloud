package io.shulie.takin.cloud.open.resp.scenemanage;

import java.io.Serializable;

import lombok.Data;

@Data
public class SceneTryRunTaskStatusResp implements Serializable {
    private Integer taskStatus;
    private String errorMsg;
}
