package io.shulie.takin.cloud.open.req.filemanager;

import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileCopyParamReq extends CloudUserCommonRequestExt {

    /**
     * 目标文件路径
     */
    private String targetPath;

    /**
     * 原文件路径
     */
    private List<String> sourcePaths;
}
