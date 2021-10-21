package io.shulie.takin.cloud.sdk.model.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 8:16 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptCheckAndUpdateReq extends ContextExt {

    /**
     * 业务请求
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String uploadPath;

    private boolean absolutePath;

    /**
     * 是否更新脚本
     */
    private boolean update;
}
