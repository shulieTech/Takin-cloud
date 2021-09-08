package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/15 下午3:28
 */
@Data
public class SceneIpNumRequest implements Serializable {

    private static final long serialVersionUID = 5601318389362884272L;

    private Integer concurrenceNum;
}
