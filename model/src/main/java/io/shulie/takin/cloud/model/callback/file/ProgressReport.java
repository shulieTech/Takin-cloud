package io.shulie.takin.cloud.model.callback.file;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 文件下载的进度上报
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ProgressReport extends Base<ProgressReport.Data> {
    /**
     * 回调类型
     */
    private CallbackType type = CallbackType.FILE_RESOURCE_PROGRESS;

    @lombok.Data
    @Accessors(chain = true)
    public static class Data {
        /**
         * 已完成
         * <p>true和false代表完成后的最终状态(成功/失败)</p>
         * <p>null代表未完</p>
         */
        private Boolean complete;
        /**
         * 文件路径
         */
        private String path;
        /**
         * 附加数据
         */
        private String attach;
        /**
         * 消息
         */
        private String message;
        /**
         * 进度
         */
        private String progress;
    }
}