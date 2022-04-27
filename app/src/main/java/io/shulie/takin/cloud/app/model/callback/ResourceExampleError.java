package io.shulie.takin.cloud.app.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.app.model.callback.basic.Basic;
import io.shulie.takin.cloud.app.model.callback.basic.ResourceExample;
import io.shulie.takin.cloud.app.model.callback.ResourceExampleError.ResourceExampleErrorInfo;

/**
 * 资源实例异常
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleError extends Basic<ResourceExampleErrorInfo> {

    private final CallbackType type = CallbackType.RESOURCE_EXAMPLE_ERROR;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ResourceExampleErrorInfo extends ResourceExample {
        private String errorMessage;

        public ResourceExampleErrorInfo(ResourceExample resourceExample) {
            setJobId(resourceExample.getJobId());
            setResourceId(resourceExample.getResourceId());
            setJobExampleId(resourceExample.getJobExampleId());
            setResourceExampleId(resourceExample.getResourceExampleId());
        }
    }
}
