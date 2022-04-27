package io.shulie.takin.cloud.app.model.notify;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.app.model.resource.ResourceSource;

/**
 * TODO
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceUpload extends Basic<List<ResourceSource>> {
}
