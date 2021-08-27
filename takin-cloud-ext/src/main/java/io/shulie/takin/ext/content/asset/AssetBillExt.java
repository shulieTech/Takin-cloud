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
    private Integer concurrenceNum;
    private TimeBean pressureTestTime;
    private Integer pressureMode;
    private TimeBean increasingTime;
    private Integer step;
    private BigDecimal avgConcurrent;
    private Integer pressureType;

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
