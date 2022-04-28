package io.shulie.takin.cloud.model.notify;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.model.notify.Metrics.MetricsInfo;

/**
 * 指标数据
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Metrics extends Basic<List<MetricsInfo>> {
    /**
     * 任务实例主键
     */
    private Long jobExampleId;

    @Data
    public static class MetricsInfo {
        /**
         * 关键词
         */
        private String transaction;
        /**
         * 请求总数
         */
        private Integer count;
        /**
         * 失败请求总数
         */
        private Integer failCount;
        /**
         * 请求数据大小
         */
        private Integer sentBytes;
        /**
         * 响应数据大小
         */
        private Integer receivedBytes;
        /**
         * 接口响应时间 - 瓶颈
         */
        private Double rt;
        /**
         * 接口响应时间 - 求和
         */
        private Double sumRt;
        /**
         * SA总数
         */
        private Integer saCount;
        /**
         * 最大接口响应时间
         */
        private Double maxRt;
        /**
         * 最小接口响应时间
         */
        private Double minRt;
        /**
         * 时间戳
         */
        private Long timestamp;
        /**
         * 活跃线程数
         */
        private Integer activeThreads;
        /**
         * 百分位数据
         */
        private String percentData;
    }
}
