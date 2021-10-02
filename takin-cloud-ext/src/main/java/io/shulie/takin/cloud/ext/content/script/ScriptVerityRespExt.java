package io.shulie.takin.cloud.ext.content.script;

import lombok.Data;

import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class ScriptVerityRespExt {

    /**
     * 错误信息列表
     */
    private List<String> errorMsg;

}
