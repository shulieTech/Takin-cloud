package io.shulie.takin.cloud.web.entrypoint.request.filemanage;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaoyong
 */
@Data
public class FileCreateByStringParamRequest {

    /**
     * 文件路径
     */
    @NotNull
    private String filePath;

    /**
     * 文件内容
     */
    @NotNull
    private String fileContent;
}
