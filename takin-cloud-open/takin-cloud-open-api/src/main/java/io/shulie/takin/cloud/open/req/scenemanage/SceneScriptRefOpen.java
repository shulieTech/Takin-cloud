package io.shulie.takin.cloud.open.req.scenemanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mubai
 * @Date: 2020-10-29 16:05
 * @Description:
 */

@Data
public class SceneScriptRefOpen implements Serializable {

    private static final long serialVersionUID = -639740802985171268L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "上传ID")
    private String uploadId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "上传时间")
    private String uploadTime;

    @ApiModelProperty(value = "上传路径")
    private String uploadPath;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "上传数据量")
    private Long uploadedData;

    @ApiModelProperty(value = "是否拆分")
    private Integer isSplit;

    @ApiModelProperty(value = "是否按顺序拆分")
    private Integer isOrderSplit;

    @ApiModelProperty(value = "Topic")
    private String topic;

    @ApiModelProperty(value = "文件类型")
    private Integer fileType;

    private String fileExtend ;

    //脚本id
    private Long scriptId;

    //文件大小
    private String fileSize ;
}
