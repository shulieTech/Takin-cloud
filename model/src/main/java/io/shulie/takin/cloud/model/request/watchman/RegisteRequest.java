package io.shulie.takin.cloud.model.request.watchman;

import io.shulie.takin.cloud.model.watchman.Register.Body;
import io.shulie.takin.cloud.model.watchman.Register.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 注册
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "调度机注册信息")
public class RegisteRequest {
    /**
     * 消息头
     */
    @Schema(description = "消息头")
    private Header header;
    /**
     * 主要消息体
     */
    @Schema(description = "主要消息体")
    private Body body;
}