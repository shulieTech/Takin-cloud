package io.shulie.takin.cloud.model.request.watchman;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 更新调度机-批量
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新调度机-批量")
public class BatchUpdateRequest extends BatchRequest {
    /**
     * 公钥
     */
    private String publicKey;
}
