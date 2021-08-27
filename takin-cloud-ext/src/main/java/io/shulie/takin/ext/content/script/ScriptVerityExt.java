package io.shulie.takin.ext.content.script;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class ScriptVerityExt implements Serializable {

    /**
     * 业务请求列表
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String scriptPath;
}
