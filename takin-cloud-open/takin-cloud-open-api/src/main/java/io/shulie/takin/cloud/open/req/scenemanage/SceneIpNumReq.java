package io.shulie.takin.cloud.open.req.scenemanage;


import io.shulie.takin.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;

/**
 * @author 无涯
 * @date 2020/10/22 8:06 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneIpNumReq extends ContextExt {

    private Integer concurrenceNum;

    private Integer tpsNum;
}
