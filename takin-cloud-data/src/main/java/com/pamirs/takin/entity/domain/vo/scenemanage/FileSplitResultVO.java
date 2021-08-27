package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class FileSplitResultVO implements Serializable {
    private Long sceneId;
    private String fileName;
    private String sliceInfo;
    private Integer sliceCount;
    private Integer isSplit;
    private Integer isOrderSplit;
}
