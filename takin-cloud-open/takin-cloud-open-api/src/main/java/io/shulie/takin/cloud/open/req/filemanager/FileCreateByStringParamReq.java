package io.shulie.takin.cloud.open.req.filemanager;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileCreateByStringParamReq extends ContextExt {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件内容
     */
    private String fileContent;
}
