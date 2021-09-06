package io.shulie.takin.cloud.biz.service.scene;

import com.pamirs.takin.entity.domain.vo.engine.EngineNotifyParam;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author 何仲奇
 * @date 2020/9/23 2:55 下午
 */
public interface EngineCallbackService {
    /**
     * 压测引擎 状态回传
     *
     * @param notify 通知参数
     * @return 状态结果
     */
    ResponseResult<?> notifyEngineState(EngineNotifyParam notify);
}
