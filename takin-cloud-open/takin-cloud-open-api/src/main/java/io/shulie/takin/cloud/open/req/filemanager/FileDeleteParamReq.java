package io.shulie.takin.cloud.open.req.filemanager;

import java.util.List;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 * 文件删除参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileDeleteParamReq extends ContextExt {

    private List<String> paths;
}
