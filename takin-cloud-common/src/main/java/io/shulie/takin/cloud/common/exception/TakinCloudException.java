package io.shulie.takin.cloud.common.exception;

import io.shulie.takin.parent.exception.entity.BaseException;
import io.shulie.takin.parent.exception.entity.ExceptionReadable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shiyajian
 * create: 2020-09-26
 */
@Getter
@Setter
public class TakinCloudException extends BaseException {

    /**
     * 错误信息
     */
    private String message;


    public TakinCloudException(ExceptionReadable ex, Object source) {
        super(ex, source);
        this.setMessage(source == null ? "" : source.toString());
    }

    public TakinCloudException(ExceptionReadable ex, Object source, Throwable e) {
        super(ex, source,e);
        this.setMessage(source == null ? "" : source.toString());
    }
}
