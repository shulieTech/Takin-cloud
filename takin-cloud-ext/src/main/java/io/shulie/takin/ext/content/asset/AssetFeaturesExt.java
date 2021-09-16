package io.shulie.takin.ext.content.asset;

import lombok.Data;

import java.io.Serializable;

/**
 * @author caijianying
 */
@Data
public class AssetFeaturesExt  implements Serializable {
    /**
     * 并发数
     */
    private Integer concurrenceNum;

    /**
     * 压测时长
     */
    private String pressureTestCost;
}
