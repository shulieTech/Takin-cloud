package io.shulie.takin.ext.content.asset;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 资产拓展模块
 * <p>
 * 业务实体
 *
 * @author 张天赐
 */
@Data
public class AssetBillExt {
    /**
     * 并发线程数
     */
    private Integer concurrenceNum;
    /**
     * 压测总时长
     */
    private TimeBean pressureTestTime;
    /**
     * 施压模式:1固定模式,2线性增长,3阶梯增长
     */
    private Integer pressureMode;
    /**
     * 递增时长
     */
    private TimeBean increasingTime;
    /**
     * 阶梯层级
     */
    private Integer step;
    /**
     * 平均并发数
     */
    private BigDecimal avgConcurrent;
    /**
     * 压测场景类型：0-2常规，3流量调试,4巡检模式,5试跑模式
     */
    private Integer pressureScene;
    /**
     * 施压类型：0并发，1TPS模式，2自定义模式
     */
    private Integer pressureType;

    /**
     * 压测总时长 1h2'34"
     */
    private String pressureTestTimeCost;

    @Data
    public static class TimeBean {
        private Long time;
        private String unit;

        private TimeBean() {

        }

        public TimeBean(Long time, String unit) {
            this.time = time;
            this.unit = unit;
        }
    }
}
