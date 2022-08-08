package io.shulie.takin.cloud.model.request.watchman;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 注册
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "调度机注册信息")
public class RegisteRequest {
    /**
     * 公钥
     */
    @Schema(description = "公钥", required = true)
    private String publicKey;
    /**
     * 附加数据
     * <p></p>
     */
    @Schema(name = "附加数据", description = "在查询中原样返回", required = true)
    private Object attach;
    /**
     * 创建时间
     * <p>默认为对象创建时间</p>
     */
    @Schema(description = "创建时间")
    private Long timeOfCreate = System.currentTimeMillis();
    /**
     * 到期时间
     * <p>默认为世界末日</p>
     */
    @Schema(description = "到期时间", defaultValue = "253402271999999")
    private Long timeOfValidity;
}