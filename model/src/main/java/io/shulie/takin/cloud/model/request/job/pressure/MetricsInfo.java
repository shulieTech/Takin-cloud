package io.shulie.takin.cloud.model.request.job.pressure;

import lombok.Data;

import org.influxdb.annotation.Column;

/**
 * 指标信息
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class MetricsInfo {
    @Column(name = "time")
    private Long time;
    /**
     * 关键词
     */
    @Column(tag = true, name = "transaction")
    private String transaction;
    /**
     * 业务活动名称
     */
    @Column(name = "test_name")
    private String testName;
    /**
     * 请求总数
     */
    @Column(name = "count")
    private Integer count;
    /**
     * 失败请求总数
     */
    @Column(name = "fail_count")
    private Integer failCount;
    /**
     * 请求数据大小
     */
    @Column(name = "sent_bytes")
    private Integer sentBytes;
    /**
     * 响应数据大小
     */
    @Column(name = "received_bytes")
    private Integer receivedBytes;
    /**
     * 接口响应时间 - 瓶颈
     */
    @Column(name = "rt")
    private Double rt;
    /**
     * 接口响应时间 - 求和
     */
    @Column(name = "sum_rt")
    private Double sumRt;
    /**
     * SA总数
     */
    @Column(name = "sa_count")
    private Integer saCount;
    /**
     * 最大接口响应时间
     */
    @Column(name = "max_rt")
    private Double maxRt;
    /**
     * 最小接口响应时间
     */
    @Column(name = "min_rt")
    private Double minRt;
    /**
     * 时间戳
     */
    @Column(name = "timestamp")
    private Long timestamp;
    /**
     * 活跃线程数
     */
    @Column(name = "active_threads")
    private Integer activeThreads;
    /**
     * 百分位数据
     */
    @Column(name = "percent_data")
    private String percentData;
    /**
     * 施压任务实例编号
     */
    @Column(tag = true, name = "pod_no")
    private String podNo;
    /**
     * 上报的数据类型
     */
    private String type;
}