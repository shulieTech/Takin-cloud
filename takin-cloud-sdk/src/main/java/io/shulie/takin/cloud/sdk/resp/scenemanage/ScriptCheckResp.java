package io.shulie.takin.cloud.sdk.resp.scenemanage;

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
