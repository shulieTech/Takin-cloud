package io.shulie.takin.ext.content.enginecall;

import io.shulie.takin.ext.content.AbstractEntry;
import lombok.Data;

/**
 * @Author: liyuanba
 * @Date: 2021/11/3 9:50 上午
 */
@Data
public class PtlLogConfigExt extends AbstractEntry {
    /**
     * 是否输出ptl日志文件
     */
    private boolean ptlFileEnable;

    /**
     * ptl日志文件是否只输出错误信息
     */
    private boolean ptlFileErrorOnly;

    /**
     * ptl日志是否只输出接口调用时间较长信息
     */
    private boolean ptlFileTimeoutOnly;

    /**
     * ptl日志接口超时阈值
     */
    private Long timeoutThreshold;

    /**
     * ptl日志是否截断
     */
    private boolean logCutOff;
}
