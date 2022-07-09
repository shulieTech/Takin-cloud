package io.shulie.takin.cloud.biz.service.machine;

import com.pamirs.takin.entity.domain.vo.machine.MachineAddParam;
import com.pamirs.takin.entity.domain.vo.machine.MachineUpdateParam;
import io.shulie.takin.cloud.ext.content.enginecall.NodeMetrics;

import java.util.List;

/**
 * ClassName:    MachineService
 * Package:    io.shulie.takin.cloud.biz.service.machine
 * Description:
 * Datetime:    2022/7/9   13:43
 * Author:   chenhongqiao@shulie.com
 */
public interface MachineService {
    List<NodeMetrics> list();

    String addNode(MachineAddParam addParam);

    Boolean updateNode(MachineUpdateParam updateParam);

    Boolean delNode(String nodeName);

    Boolean enableNode(String nodeName);

    Boolean disableNode(String nodeName);
}
