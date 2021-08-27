package io.shulie.takin.cloud.open.req.filemanager;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileCreateByStringParamReq extends CloudUserCommonRequestExt {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件内容
     */
    private String fileContent;
}
