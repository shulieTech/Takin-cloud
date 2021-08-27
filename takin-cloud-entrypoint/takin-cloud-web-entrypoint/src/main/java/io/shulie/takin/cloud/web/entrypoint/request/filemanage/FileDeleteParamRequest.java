package io.shulie.takin.cloud.web.entrypoint.request.filemanage;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 * 文件删除参数
 */
@Data
public class FileDeleteParamRequest {

    List<String> paths;
}
