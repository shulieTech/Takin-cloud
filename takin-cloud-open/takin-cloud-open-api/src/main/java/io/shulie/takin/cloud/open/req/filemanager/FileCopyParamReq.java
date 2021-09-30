package io.shulie.takin.cloud.open.req.filemanager;

import java.util.List;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileCopyParamReq extends ContextExt {

    /**
     * 目标文件路径
     */
    private String targetPath;

    /**
     * 原文件路径
     */
    private List<String> sourcePaths;
}
