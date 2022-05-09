package io.shulie.takin.cloud.model.request;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 申请资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "资源单")
public class ApplyResourceRequest {
    /**
     * 调度主键
     */
    @Schema(description = "调度主键")
    private Long watchmanId;
    /**
     * 申请的CPU
     */
    @Schema(description = "需要的CPU")
    private String cpu;
    /**
     * 申请的内存
     */
    @Schema(description = "需要的内存")
    private String memory;
    /**
     * 需要的资源数
     */
    @Schema(description = "需要的数量")
    private Integer number;
    /**
     * 申请的CPU
     */
    @Schema(description = "限制的CPU")
    private String limitCpu;
    /**
     * 申请的内存
     */
    @Schema(description = "限制的内存")
    private String limitMemory;
    /**
     * 状态回调地址
     */
    @Schema(description = "状态回调地址")
    String callbackUrl;
    /**
     * 资源镜像
     */
    @Schema(description = "资源镜像")
    String image;
}
