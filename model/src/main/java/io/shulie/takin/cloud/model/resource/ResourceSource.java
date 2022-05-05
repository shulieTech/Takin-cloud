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
     * <p>1代表一个逻辑核</p>
     */
    @Schema(description = "CPU")
    String cpu;
    /**
     * 内存
     * <p>1代码1Kb</p>
     */
    @Schema(description = "内存")
    String memory;
}
