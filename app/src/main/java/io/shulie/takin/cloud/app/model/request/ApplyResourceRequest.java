package io.shulie.takin.cloud.app.model.request;

import lombok.Data;

/**
 * 申请资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class ApplyResourceRequest {
    /**
     * 调度主键
     */
    private Long watchmanId;
    /**
     * 申请的CPU
     */
    private String cpu;
    /**
     * 申请的内存
     */
    private String memory;
    /**
     * 需要的资源数
     */
    private Integer number;
    /**
     * 申请的CPU
     */
    private String limitCpu;
    /**
     * 申请的内存
     */
    private String limitMemory;
}
