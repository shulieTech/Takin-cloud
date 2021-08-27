package io.shulie.takin.cloud.biz.output.scenetask;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
public class SceneTaskQueryTpsOutput implements Serializable {
    private static final long serialVersionUID = -7691499995105603643L;

    /**
     * 总的tps值
     */
    private Long totalTps;
}
