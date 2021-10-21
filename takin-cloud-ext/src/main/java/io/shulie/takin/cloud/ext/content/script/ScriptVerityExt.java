package io.shulie.takin.cloud.ext.content.script;


import lombok.Data;

import java.util.List;

/**
 * @author zhaoyong
 */
@Data
public class ScriptVerityExt {

    /**
     * 业务请求列表
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String scriptPath;
}
