package io.shulie.takin.cloud.sdk.model.response.scenemanage;

import java.util.List;

import lombok.Data;

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
