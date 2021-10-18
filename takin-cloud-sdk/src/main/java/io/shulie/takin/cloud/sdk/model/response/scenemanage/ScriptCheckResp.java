package io.shulie.takin.cloud.sdk.model.response.scenemanage;

import lombok.Data;

import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class ScriptCheckResp {
    /**
     * 错误信息列表
     */
    private List<String> errorMsg;
}
