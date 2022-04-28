package io.shulie.takin.cloud.model.resource;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.constant.enums.ResourceExampleStatus;

/**
 * 资源实例概览
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "资源实例概览")
public class ResourceExampleOverview {
    /**
     * 资源实例名称
     */
    @Schema(description = "名称")
    private String name;
    /**
     * 资源实例状态
     */
    @Schema(description = "状态")
    private ResourceExampleStatus status;
    /**
     * 状态对应的时间
     */
    @Schema(description = "所处状态对应的变更时间")
    private Long statusTime;
    /**
     * 状态对应的信息
     * <p>错误信息</p>
     */
    @Schema(description = "所处状态对应的信息(错误信息)")
    private String statusMessage;
    /**
     * 重启次数
     * <p>暂未实现</p>
     */
    @Schema(description = "重启次数")
    private Integer restart = 0;
    /**
     * 启动时间
     */
    @Schema(description = "启动时间")
    private Long startTime;
    /**
     * 私域IP
     */
    @Schema(description = "私域IP(pod的ip)")
    private String ip;
    /**
     * 开放域IP
     */
    @Schema(description = "开放域IP(宿主机的ip)")
    private String hostIp;
}

