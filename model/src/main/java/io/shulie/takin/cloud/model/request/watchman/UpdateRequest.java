package io.shulie.takin.cloud.model.request.watchman;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 更新调度机
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "更新调度机")
public class UpdateRequest {
    /**
     * 主键
     */
    private Long id;
    /**
     * 公钥
     */
    private String publicKey;
}
