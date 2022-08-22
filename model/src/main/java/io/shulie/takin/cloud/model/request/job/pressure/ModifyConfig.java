package io.shulie.takin.cloud.model.request.job.pressure;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.ThreadConfigInfo;

/**
 * 修改线程组配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "修改线程组配置")
public class ModifyConfig {
    /**
     * 施压任务主键
     */
    @Schema(description = "任务主键")
    private Long pressureId;
    /**
     * 关键字
     */
    @Schema(description = "关键词")
    private String ref;
    /**
     * 线程组类型
     */
    @Schema(description = "线程组类型")
    private ThreadGroupType type;
    /**
     * 配置内容
     */
    @Schema(description = "配置内容")
    private ThreadConfigInfo context;
}
