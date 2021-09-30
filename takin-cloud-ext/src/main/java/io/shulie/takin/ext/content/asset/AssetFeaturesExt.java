package io.shulie.takin.ext.content.asset;

import io.swagger.annotations.ApiModelProperty;
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

    /**
     * 脚本调试ID
     */
    private Long scriptDebugId;
}
