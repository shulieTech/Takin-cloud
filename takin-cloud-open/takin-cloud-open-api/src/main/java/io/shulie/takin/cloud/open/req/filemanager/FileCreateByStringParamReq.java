package io.shulie.takin.cloud.open.req.filemanager;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
