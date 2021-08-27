package io.shulie.takin.ext.content.script;

import java.io.Serializable;

import lombok.Data;

/**
 * @ClassName ScriptUrlVO
 * @Description
 * @Author qianshui
 * @Date 2020/4/22 上午4:14
 */
@Data
public class ScriptUrlExt implements Serializable {

    private static final long serialVersionUID = 2155178590508223791L;

    private String name;

    private Boolean enable;

    private String path;

    public ScriptUrlExt() {

    }

    public ScriptUrlExt(String name, Boolean enable, String path) {
        this.name = name;
        this.enable = enable;
        this.path = path;
    }
}
