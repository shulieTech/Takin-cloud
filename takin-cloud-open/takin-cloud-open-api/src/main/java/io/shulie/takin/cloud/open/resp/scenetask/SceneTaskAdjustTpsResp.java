package io.shulie.takin.cloud.open.resp.scenetask;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
public class SceneTaskAdjustTpsResp implements Serializable {
    private static final long serialVersionUID = 5190569177892521242L;

    /**
     * 总的tps值
     */
    private Long totalTps;
}
