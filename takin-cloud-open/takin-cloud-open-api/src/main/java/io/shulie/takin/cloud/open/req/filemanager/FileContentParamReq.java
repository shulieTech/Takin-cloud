package io.shulie.takin.cloud.open.req.filemanager;

import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.req.filemanager
 * @date 2020/12/9 10:50 上午
 */
@Data
public class FileContentParamReq  extends CloudUserCommonRequestExt {
    private List<String> paths;
}
