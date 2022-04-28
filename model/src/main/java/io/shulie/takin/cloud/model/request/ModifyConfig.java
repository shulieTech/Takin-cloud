package io.shulie.takin.cloud.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.model.response.JobConfig;

/**
 * 修改线程组配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "修改线程组配置")
public class ModifyConfig extends JobConfig {
}
