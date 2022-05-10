package io.shulie.takin.cloud.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.model.callback.basic.ResourceExample;
import io.shulie.takin.cloud.model.callback.ResourceExampleError.ResourceExampleErrorInfo;

/**
 * 资源实例异常
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ResourceExampleError extends Base<ResourceExampleErrorInfo> {
    private CallbackType type = CallbackType.RESOURCE_EXAMPLE_ERROR;

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
