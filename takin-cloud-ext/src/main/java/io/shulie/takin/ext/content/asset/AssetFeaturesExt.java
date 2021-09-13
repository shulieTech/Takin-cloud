package io.shulie.takin.ext.content.asset;

import java.io.Serializable;

/**
 * @author caijianying
 */
public class AssetFeaturesExt  implements Serializable {
    /**
     * @see io.shulie.takin.ext.content.enums.AssetTypeEnum
     */
    private Integer assetType;

    /**
     * 压测报告 -> 场景名称/报告ID
     * 业务活动流量验证 -> 业务活动名称/业务活动ID
     * 脚本调试 ->  脚本名称/脚本ID
     * 巡检场景 -> 场景名称/场景ID
     *
     * 来源
     */
    private Long resourceName;
    private Long resourceId;

    /**
     * 并发数
     */
    private Integer concurrenceNum;

    /**
     * 压测时长
     */
    private String pressureTestCost;
}
