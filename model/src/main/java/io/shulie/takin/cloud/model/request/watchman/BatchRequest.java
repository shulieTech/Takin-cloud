package io.shulie.takin.cloud.model.request.watchman;

import java.util.List;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

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
    @NotNull(message = "调度器主键集合不能为空")
    @Size(min = 1, message = "调度器主键集合不能为空")
    private List<Long> watchmanIdList;
}
