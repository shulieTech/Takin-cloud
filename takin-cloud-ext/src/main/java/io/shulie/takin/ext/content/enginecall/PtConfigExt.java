package io.shulie.takin.ext.content.enginecall;

import io.shulie.takin.ext.content.AbstractEntry;
import lombok.Data;

import java.util.Map;

/**
 * 施压配置
 * @Author: liyuanba
 * @Date: 2021/10/29 9:51 上午
 */
@Data
public class PtConfigExt extends AbstractEntry {
    /**
     * pod数
     */
    private Integer podNum;
    /**
     * 压测时长
     */
    private Long duration;
    /**
     * 压测时长单位
     */
    private String unit;
    /**
     * 流量预估：各个线程组的总和
     */
    private Double estimateFlow;
    /**
     * 线程组配置
     */
    private Map<String, PressureConfigExt> threadGroupConfig;
}
