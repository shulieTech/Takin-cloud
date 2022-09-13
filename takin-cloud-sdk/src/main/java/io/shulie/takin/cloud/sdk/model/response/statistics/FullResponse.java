package io.shulie.takin.cloud.sdk.model.response.statistics;

import java.util.List;
import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 全量统计的响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class FullResponse {
    /**
     * 可运行的场景数量
     */
    private int sceneRunableCount = 0;
    /**
     * 运行中的场景数量
     */
    private int sceneRunningCount = 0;
    /**
     * 可运行的场景比例
     */
    private String sceneRunableProportion;
    /**
     * 运行中的场景比例
     */
    private String sceneRunningProportion;
    /**
     * 结论为通过的报告数量
     */
    private int reportConclusionTrueCount = 0;
    /**
     * 结论为不通过的报告数量
     */
    private int reportConclusionFalseCount = 0;
    /**
     * 结论为通过的报告比例
     */
    private String reportConclusionTrueProportion;
    /**
     * 结论为不通过的报告比例
     */
    private String reportConclusionFalseProportion;
    /**
     * 头部榜单
     */
    private List<TopItem> topList = new ArrayList<>(0);

    /**
     * 榜单数据
     */
    @Data
    public static class TopItem {
        /**
         * 场景主键
         */
        private Long sceneId;
        /**
         * 场景名称
         */
        private String sceneName;
        /**
         * 场景创建的时间
         */
        private Long sceneCreateTime;
        /**
         * 创建场景的用户主键
         */
        private Long sceneCreateUserId;
        /**
         * 报告数量
         */
        private int reportCount = 0;
        /**
         * 结论为通过的报告数量
         */
        private int reportConclusionTrueCount = 0;
        /**
         * 结论为不通过的报告数量
         */
        private int reportConclusionFalseCount = 0;
        /**
         * 最后一次运行的时间
         */
        private long lastTime = 0;
    }
}
