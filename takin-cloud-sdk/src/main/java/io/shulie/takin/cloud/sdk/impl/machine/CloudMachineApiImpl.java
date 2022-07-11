package io.shulie.takin.cloud.sdk.impl.machine;

import com.alibaba.fastjson.TypeReference;
import io.shulie.takin.cloud.entrypoint.machine.CloudMachineApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineAddReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineBaseReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.machine.NodeMetricsResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportResp;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ClassName:    CloudMachineApiImpl
 * Package:    io.shulie.takin.cloud.sdk.impl.machine
 * Description:
 * Datetime:    2022/7/9   14:20
 * Author:   chenhongqiao@shulie.com
 */
@Service
public class CloudMachineApiImpl implements CloudMachineApi {
    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public ResponseResult<List<NodeMetricsResp>> list(ContextExt req) {
        ResponseResult<List<NodeMetricsResp>> result = cloudApiSenderService.get(
                EntrypointUrl.join(EntrypointUrl.MODULE_MACHINE, EntrypointUrl.METHOD_MACHINE_LIST),
                req, new TypeReference<ResponseResult<List<NodeMetricsResp>>>() {});
        return ResponseResult.success(result.getData(), result.getTotalNum());
    }

    @Override
    public String add(MachineAddReq req) {
        return cloudApiSenderService.post(
                EntrypointUrl.join(EntrypointUrl.MODULE_MACHINE, EntrypointUrl.METHOD_MACHINE_ADD),
                req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    @Override
    public Boolean update(MachineUpdateReq req) {
        return cloudApiSenderService.post(
                EntrypointUrl.join(EntrypointUrl.MODULE_MACHINE, EntrypointUrl.METHOD_MACHINE_UPDATE),
                req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    @Override
    public Boolean delete(MachineBaseReq req) {
       return cloudApiSenderService.get(
                EntrypointUrl.join(EntrypointUrl.MODULE_MACHINE, EntrypointUrl.METHOD_MACHINE_DELETE),
               req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    @Override
    public Boolean enable(MachineBaseReq req) {
        return cloudApiSenderService.get(
                EntrypointUrl.join(EntrypointUrl.MODULE_MACHINE, EntrypointUrl.METHOD_MACHINE_ENABLE),
                req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    @Override
    public Boolean disable(MachineBaseReq req) {
        return cloudApiSenderService.get(
                EntrypointUrl.join(EntrypointUrl.MODULE_MACHINE, EntrypointUrl.METHOD_MACHINE_DISABLE),
                req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }
}
