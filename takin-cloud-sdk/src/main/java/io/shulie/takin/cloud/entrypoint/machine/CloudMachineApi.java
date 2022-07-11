package io.shulie.takin.cloud.entrypoint.machine;

import io.shulie.takin.cloud.ext.content.enginecall.NodeMetrics;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineAddReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineBaseReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.machine.NodeMetricsResp;
import io.shulie.takin.common.beans.response.ResponseResult;

import java.util.List;

/**
 * ClassName:    CloudMachineApi
 * Package:    io.shulie.takin.cloud.entrypoint.machine
 * Description:
 * Datetime:    2022/7/9   14:14
 * Author:   chenhongqiao@shulie.com
 */
public interface CloudMachineApi {
    ResponseResult<List<NodeMetricsResp>> list(ContextExt req);

    String add(MachineAddReq req);

    Boolean update(MachineUpdateReq req);

    Boolean delete(MachineBaseReq req);

    Boolean enable(MachineBaseReq req);

    Boolean disable(MachineBaseReq req);
}
