package io.shulie.takin.cloud.open.req.scenemanage;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.bean.scenemanage
 * @description:
 * @date 2020/10/22 8:06 下午
 */
@Data
public class SceneIpNumReq extends CloudUserCommonRequestExt implements Serializable {

    private static final long serialVersionUID = 5601318389362884272L;

    private Integer concurrenceNum;

    private Integer tpsNum;
}
