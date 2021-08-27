package io.shulie.takin.cloud.open.resp.scenemanage;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class ScriptCheckResp implements Serializable {
    /**
     * 错误信息列表
     */
    private List<String> errorMsg;
}
