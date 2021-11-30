package io.shulie.takin.cloud.ext.content.script;

import java.util.List;

import lombok.Data;

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
