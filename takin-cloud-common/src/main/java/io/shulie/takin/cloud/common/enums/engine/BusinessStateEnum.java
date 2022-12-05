package io.shulie.takin.cloud.common.enums.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态枚举
 *
 * @author chenhongqiao@shulie.com
 */
@Getter
@AllArgsConstructor
public enum BusinessStateEnum {
    /**
     * 申请资源
     */
    APPLY("apply", "申请资源"),
    /**
     * 准备压测
     */
    READIED("readied", "准备就绪"),
    /**
     * 压测失败｜中断失败
     */
    FAILED("failed", "失败"),
    /**
     * 压测结束
     */
    SUCCESSFUL("successful", "成功"),
    /**
     * 开始压测｜压测中
     */
    PRESSURE("pressure", "压测"),
    /**
     * 准备停止｜停止中
     */
    INTERRUPT("interrupt", "中断");

    private final String state;
    private final String msg;
}
