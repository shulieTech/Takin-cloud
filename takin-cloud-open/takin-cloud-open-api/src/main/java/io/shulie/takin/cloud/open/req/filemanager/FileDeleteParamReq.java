package io.shulie.takin.cloud.open.req.filemanager;

import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author zhaoyong
 * 文件删除参数
 */
@Data
public class FileDeleteParamReq extends CloudUserCommonRequestExt {

    private List<String> paths;
}
