package io.shulie.takin.cloud.common.enums.middleware;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 比对结果枚举
 *
 * @author liuchuan
 * @date 2021/6/1 2:26 下午
 */
@Getter
@AllArgsConstructor
public enum CompareMiddlewareJarStatusEnum {

    /**
     * 无需支持
     */
    NO_REQUIRED("无需支持"),
    /**
     * 未录入
     */
    NO("未录入"),
    /**
     * 待验证
     */
    TO_BE_VERIFIED("待验证"),
    /**
     * 待支持
     */
    TO_BE_SUPPORTED("待支持"),
    /**
     * 已支持
     */
    SUPPORTED("已支持");

    private final String desc;

}
