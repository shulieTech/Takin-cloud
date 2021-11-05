package io.shulie.takin.ext.content.response;

import io.shulie.takin.ext.content.AbstractEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: liyuanba
 * @Date: 2021/11/5 5:05 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Response<T> extends AbstractEntry {
    private boolean success = true;
    private String code;
    private String msg;
    private T data;
}
