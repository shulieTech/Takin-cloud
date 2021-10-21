package io.shulie.takin.cloud.sdk.model.request.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * TODO
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteTempRequest extends ContextExt {
    @ApiModelProperty(value = "上传文件ID")
    private String uploadId;

    @ApiModelProperty(value = "Topic")
    private String topic;
}
