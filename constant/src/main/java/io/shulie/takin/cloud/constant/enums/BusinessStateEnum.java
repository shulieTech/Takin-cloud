package io.shulie.takin.cloud.constant.enums;

import lombok.Data;
import lombok.Getter;

/**
 * ClassName:    BusinessState
 * Package:    io.shulie.takin.drilling.sdk.common.enums.resources
 * Description:
 * Datetime:    2022/4/27   16:03
 * Author:   chenhongqiao@shulie.com
 */
@Getter
public enum BusinessStateEnum {

    APPLY("apply", "申请资源"),    //申请资源
    READIED("readied", "准备就绪"),    //准备压测
    FAILED("failed", "失败"),         //压测失败｜中断失败
    SUCCESSFUL("successful", "成功"), //压测结束
    PRESSURE("pressure", "压测"),     //开始压测｜压测中
    INTERRUPT("interrupt", "中断");   //准备停止｜停止中

    private String state;
    private String msg;

    BusinessStateEnum(String state, String msg) {
        this.state = state;
        this.msg = msg;
    }
}
