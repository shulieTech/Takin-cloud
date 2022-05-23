package io.shulie.takin.cloud.data.param.scenemanage;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author moriarty
 */
@Data
@Accessors(chain = true)
public class SceneBigFileSliceParam {
    private Long id;
    private Long sceneId;
    private Long fileRefId;
    private Integer sliceCount;
    private String fileName;
    private String filePath;
    private String sliceInfo;
    private Integer status;
    private Date fileUploadTime;
    private Integer isOrderSplit;
    private Integer isSplit;
    private String fileMd5;
}
