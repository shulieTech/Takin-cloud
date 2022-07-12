package io.shulie.takin.cloud.entrypoint.controller.machine;

import com.pamirs.takin.entity.domain.vo.machine.MachineAddParam;
import com.pamirs.takin.entity.domain.vo.machine.MachineUpdateParam;
import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.cloud.biz.service.machine.MachineService;
import io.shulie.takin.cloud.ext.content.enginecall.NodeMetrics;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:    MachineController
 * Package:    io.shulie.takin.cloud.entrypoint.controller.machine
 * Description:
 * Datetime:    2022/7/9   13:45
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
@RestController
@Api(tags = "压测机器", value = "压测机器")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_MACHINE)
public class MachineController {

    @Resource
    private MachineService machineService;

    @GetMapping(EntrypointUrl.METHOD_MACHINE_LIST)
    @ApiOperation(value = "压测机器列表")
    public ResponseResult<List<NodeMetrics>> list() {
        return ResponseResult.success(machineService.list());
    }

    @PostMapping(EntrypointUrl.METHOD_MACHINE_ADD)
    @ApiOperation(value = "添加压测机")
    public ResponseResult<String> add(@RequestBody MachineAddParam addParam) {
        String res = machineService.addNode(addParam);
        if (Objects.equals(res, "添加node成功")) {
            return ResponseResult.success(res);
        } else {
            return ResponseResult.fail(res, "");
        }
    }


    @PostMapping(EntrypointUrl.METHOD_MACHINE_UPDATE)
    @ApiOperation(value = "修改压测机")
    public ResponseResult<Boolean> update(@RequestBody MachineUpdateParam updateParam) {
        return ResponseResult.success(machineService.updateNode(updateParam));
    }

    @GetMapping(EntrypointUrl.METHOD_MACHINE_DELETE)
    @ApiOperation(value = "删除压测机")
    public ResponseResult<Boolean> delete(@RequestParam(value = "nodeName") String nodeName) {
        return ResponseResult.success(machineService.delNode(nodeName));
    }

    @GetMapping(EntrypointUrl.METHOD_MACHINE_ENABLE)
    @ApiOperation(value = "启用压测机")
    public ResponseResult<Boolean> enable(@RequestParam(value = "nodeName") String nodeName) {
        return ResponseResult.success(machineService.enableNode(nodeName));
    }

    @GetMapping(EntrypointUrl.METHOD_MACHINE_DISABLE)
    @ApiOperation(value = "禁用压测机")
    public ResponseResult<Boolean> disable(@RequestParam(value = "nodeName") String nodeName) {
        return ResponseResult.success(machineService.disableNode(nodeName));
    }

}
