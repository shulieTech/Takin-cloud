package io.shulie.takin.cloud.app.service;

import io.shulie.takin.cloud.model.request.ScriptBuildRequest;

/**
 * ClassName:    ScriptService
 * Package:    io.shulie.takin.cloud.app.service
 * Description: 脚本服务
 * Datetime:    2022/5/19   11:30
 * Author:   chenhongqiao@shulie.com
 */
public interface ScriptService {

    String buildJmeterScript(ScriptBuildRequest scriptRequest);
}
