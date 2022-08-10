package io.shulie.takin.cloud.model.resource;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 资源 - String
 * <ul>
 *     <li>CPU</li>
 *     <li>内存</li>
 * </ul>
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "资源信息")
public class ResourceSource {
    /**
     * CPU
     * <p>1/1C代表一个逻辑核</p>
     * <p>0.1/100m代表100微核</p>
     */
    @Schema(description = "CPU")
    String cpu;
    /**
     * 内存
     * <p>1代码1Kb</p>
     */
    @Schema(description = "内存")
    String memory;
    /**
     * 资源类型
     */
    @Schema(description = "资源类型")
    String type;
    /**
     * 名称
     */
    @Schema(description = "调度机名称")
    String name;
    /**
     * nfs服务端地址
     */
    @Schema(description = "nfs服务端地址")
    private String nfsServer;
    /**
     * nfs服务端文件夹
     */
    @Schema(description = "nfs服务端文件夹")
    private String nfsDir;

    /**
     * nfs已使用容量
     */
    @Schema(description = "nfs已使用容量")
    private Long nfsUsableSpace;
    /**
     * nfs总容量
     */
    @Schema(description = "nfs总容量")
    private Long nfsTotalSpace;
}
