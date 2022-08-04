package io.shulie.takin.cloud.model.response.watchman;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.model.resource.Resource;

/**
 * 列表响应
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class ListResponse {
    /**
     * 调度机标识
     */
    @Schema(description = "调度机标识")
    private Long id;
    /**
     * 关键词
     */
    @Schema(description = "关键词")
    private String ref;
    /**
     * 关键词签名
     */
    @Schema(description = "关键词签名")
    private String refSign;
    /**
     * 资源
     */
    @Schema(description = "资源总量")
    private Resource resource;
}
