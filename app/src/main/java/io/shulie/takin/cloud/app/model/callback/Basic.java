package io.shulie.takin.cloud.app.model.callback;

import lombok.Data;

/**
 * 基类
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
public class Basic<T> {
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
}
