package io.shulie.takin.cloud.data.result.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @ClassName ScriptCheckResult
 * @Description
 * @Author qianshui
 * @Date 2020/4/20 下午8:24
 */
@Data
public class ScriptCheckResult implements Serializable {

    private static final long serialVersionUID = 9156507579287192742L;

    private Boolean matchActivity = true;

    private Boolean ptTag = true;

    private String errmsg;

    public ScriptCheckResult() {

    }

    public ScriptCheckResult(Boolean matchActivity, Boolean ptTag, String errmsg) {
        this.matchActivity = matchActivity;
        this.ptTag = ptTag;
        this.errmsg = errmsg;
    }
}
