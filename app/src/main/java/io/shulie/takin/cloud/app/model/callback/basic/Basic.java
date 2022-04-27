package io.shulie.takin.cloud.app.model.callback.basic;

import lombok.Data;

import io.shulie.takin.cloud.constant.enums.CallbackType;

/**
 * 基类
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public abstract class Basic<T> {
    public Basic() {
        setSign("");
        setTime(System.currentTimeMillis());
        setCallbackTime(getTime());
    }

    /**
     * 事件发生的时间
     */
    private Long time;
    /**
     * 发起回调的时间
     */
    private Long callbackTime;
    /**
     * 签名
     */
    private String sign;
    /**
     * 数据
     */
    private T data;

    /**
     * 上报类型
     *
     * @return 上报类型
     */
    public abstract CallbackType getType();
}
