package io.shulie.takin.cloud.sdk.req.filemanager;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
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
