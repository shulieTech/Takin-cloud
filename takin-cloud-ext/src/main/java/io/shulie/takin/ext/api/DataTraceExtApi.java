package io.shulie.takin.ext.api;

import io.shulie.takin.ext.content.trace.ContextExt;
import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;

/**
 * @author hezhongqi
 * @author 张天赐
 * @date 2021/7/29 20:30
 */
public interface DataTraceExtApi extends ExtensionPoint {

    /**
     * 获取数据溯源上下文
     *
     * @return 当前的溯源上下文
     */
    ContextExt getContext();

}
