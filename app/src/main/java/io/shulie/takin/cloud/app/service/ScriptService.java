package io.shulie.takin.cloud.app.service;

import io.shulie.takin.cloud.model.request.ScriptBuildRequest;
import io.shulie.takin.cloud.model.request.ScriptCheckRequest;
import io.shulie.takin.cloud.model.response.ApiResult;

/**
 * 脚本服务
 *
 * @author chenhongqiao@shulie.com
 * @date 2022/5/19   11:30
 */
public interface ScriptService {

    /**
     * 构建普通Jmeter脚本
     *
     * @param scriptRequest
     * @return
     */
    String buildJmeterScript(ScriptBuildRequest scriptRequest);

    /**
     * 构建压测Jmeter脚本
     *
     * @return
     */
    String buildPressureJmeterScript();

    /**
     * 校验Jmeter脚本
     *
     * @return
     */
    ApiResult<Object> checkJmeterScript(ScriptCheckRequest scriptCheckRequest);
}
