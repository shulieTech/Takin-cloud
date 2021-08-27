package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ScriptFileSplitVO implements Serializable {

    private Long sceneId;

    private String fileName;

    private Boolean split;

    private Boolean orderSplit;

    private Integer podNum;

    private Integer orderColumnNum;

    private String columnSeparator;

    private Boolean forceSplit;

}
