package com.pamirs.takin.entity.domain.dto.report;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-04-21
 */
@Data
public class StatReportDTO {

    /**
     * 时间
     */
    private String time;

    /**
     * 总请求
     */
    private Long totalRequest;

    /**
     * 失败的总次数
     */
    private Long failRequest;

    /**
     * 平均线程数
     */
    private BigDecimal avgConcurrenceNum;

    /**
     * tps
     */
    private BigDecimal tps;

    /**
     * 平均rt
     */
    private BigDecimal avgRt;

    /**
     * Sa总计数
     */
    private BigDecimal saCount;

    /**
     * minRt
     */
    private BigDecimal minRt;

    /**
     * maxRt
     */
    private BigDecimal maxRt;

    /**
     * maxTps
     */
    private BigDecimal maxTps;

    /**
     * 查询记录数
     */
    private Long recordCount;

    /**
     * 获取SA
     * sa总数/总请求*100
     *
     * @return
     */
    public BigDecimal getSa() {
        if (saCount == null) {
            return null;
        }
//        return saCount.divide(new BigDecimal(getTotalRequest()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        //fixed by lipeng  20210623  这里修改问题和成功率一样
        return saCount.divide(new BigDecimal(getTotalRequest()), 4, RoundingMode.DOWN).multiply(new BigDecimal(100));
        //fixed end
    }

    /**
     * 成功率
     * (总次数-失败次数)/总次数*100
     *
     * @return
     */
    public BigDecimal getSuccessRate() {
        if (getTotalRequest() == null) {
            return null;
        }
//        return new BigDecimal(getTotalRequest() - getFailRequest()).divide(new BigDecimal(getTotalRequest()), 4,
//            RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        //fixed by lipeng  20210623 4个精度 如果是99.99998四舍五入会变成100%，所以这里RoundingMode.HALF_UP改为RoundingMode.DOWN
        // 舍去4位精度后小数而不是四舍五入， 这个处理方法如果成功率只有0.00009时成功率会变成0.00，这里和产品三变沟通过，可以这么处理
        return new BigDecimal(getTotalRequest() - getFailRequest()).divide(new BigDecimal(getTotalRequest()), 4,
                RoundingMode.DOWN).multiply(new BigDecimal(100));
        //fixed end
    }

}
