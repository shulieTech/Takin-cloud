package io.shulie.takin.cloud.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.model.request.StartRequest.ThreadConfigInfo;

/**
 * 施压任务配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "配置内容体")
public class PressureConfig {
    /**
     * 任务主键
     */
    @Schema(description = "任务主键")
    private Long jobId;
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
