package io.shulie.takin.cloud.biz.service.machine.impl;

import com.pamirs.takin.entity.domain.vo.machine.MachineAddParam;
import com.pamirs.takin.entity.domain.vo.machine.MachineUpdateParam;
import io.shulie.takin.cloud.biz.service.machine.MachineService;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.ext.content.enginecall.NodeMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ClassName:    MachineServiceImpl
 * Package:    io.shulie.takin.cloud.biz.service.machine.impl
 * Description:
 * Datetime:    2022/7/9   13:54
 * Author:   chenhongqiao@shulie.com
 */
@Service
@Slf4j
public class MachineServiceImpl implements MachineService {
    @Resource
    private EnginePluginUtils pluginUtils;

    @Override
    public List<NodeMetrics> list() {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.getNodeMetrics();
    }

    @Override
    public String addNode(MachineAddParam addParam) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.addNode(addParam.getNodeIp(), addParam.getName(), addParam.getPassword());
    }

    @Override
    public Boolean updateNode(MachineUpdateParam updateParam) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.updateNode(updateParam.getNodeName(), updateParam.getUpdateName());
    }

    @Override
    public Boolean delNode(String nodeName) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.deleteNode(nodeName);
    }

    @Override
    public Boolean enableNode(String nodeName) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.enableNode(nodeName);
    }

    @Override
    public Boolean disableNode(String nodeName) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.disableNode(nodeName);
    }
}
