package io.shulie.takin.cloud.model.request.watchman;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 批量操作的请求
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class BatchRequest {
    /**
     * 调度器主键集合
     */
    @Schema(description = "调度器主键集合")
    private List<Long> watchmanIdList;
}
