package io.shulie.takin.cloud.model.callback;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.model.callback.basic.Base;
import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 数据校准任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@lombok.Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Calibration extends Base<Calibration.Data> {
    /**
     * 回调类型
     */
    private CallbackType type = CallbackType.CALIBRATION;

    @lombok.Data
    @Accessors(chain = true)
    public static class Data {
        /**
         * 数据校准任务主键
         */
        private long id;
        /**
         * 施压任务主键
         */
        private long pressureId;
        /**
         * 资源主键
         */
        private long resourceId;
        /**
         * 执行结果
         */
        private String content;
        /**
         * 是否完成
         */
        private Boolean completed;
    }
}
