package io.shulie.takin.cloud.common.request.scenemanage;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.common.constants.ValidConstants;
import io.shulie.takin.cloud.common.pojo.dto.scenemanage.UploadFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/4/25 10:16 上午
 */
@Data
@ApiModel("请求类-更新场景对应的脚本文件")
public class UpdateSceneFileRequest {

    /**
     * 脚本发布id, 弃用
     */
    @Deprecated
    @ApiModelProperty(hidden = true)
    private Long scriptId;

    @ApiModelProperty("新的脚本发布id")
    @NotNull(message = "新的脚本发布id" + ValidConstants.MUST_NOT_BE_NULL)
    private Long newScriptId;

    @ApiModelProperty("旧的脚本发布id")
    @NotNull(message = "旧的脚本发布id" + ValidConstants.MUST_NOT_BE_NULL)
    private Long oldScriptId;

    @ApiModelProperty("脚本类型")
    @NotNull(message = "脚本类型" + ValidConstants.MUST_NOT_BE_NULL)
    private Integer scriptType;

    @ApiModelProperty("上传文件")
    @NotEmpty(message = "上传文件不能为空")
    private List<UploadFileDTO> uploadFiles;

    @ApiModelProperty("是否覆盖大文件 1=覆盖 0=不覆盖")
    @NotNull(message = "是否覆盖大文件" + ValidConstants.MUST_NOT_BE_NULL)
    private Integer ifCoverBigFile;

}
