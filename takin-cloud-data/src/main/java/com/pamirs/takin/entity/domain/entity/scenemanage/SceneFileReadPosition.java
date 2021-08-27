package com.pamirs.takin.entity.domain.entity.scenemanage;

import lombok.Data;

@Data
public class SceneFileReadPosition {
    /**
     *   分片开始位置
     */
    private Long startPosition;

    /**
     * 分片已经读取的位置
     */
    private Long readPosition;

    /**
     * 分片结束位置
     */
    private Long endPosition;
}
