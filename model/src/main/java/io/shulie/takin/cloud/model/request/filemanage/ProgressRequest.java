package io.shulie.takin.cloud.model.request.filemanage;

import lombok.Data;

/**
 * 进度
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class ProgressRequest {
    /**
     * 标识
     */
    Long id;
    /**
     * 总大小
     */
    Long totalSize;
    /**
     * 完成的大小
     */
    Long completeSize;
    /**
     * 已完成
     */
    Boolean completed;
}
