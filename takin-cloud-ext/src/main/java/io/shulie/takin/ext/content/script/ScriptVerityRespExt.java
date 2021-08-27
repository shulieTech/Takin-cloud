package io.shulie.takin.ext.content.script;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class ScriptVerityRespExt implements Serializable {

    /**
     * 错误信息列表
     */
    private List<String> errorMsg;

}
