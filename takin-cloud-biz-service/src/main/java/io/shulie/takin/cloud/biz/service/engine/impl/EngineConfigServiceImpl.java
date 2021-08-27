package io.shulie.takin.cloud.biz.service.engine.impl;

import java.util.List;
import java.util.Objects;

import io.shulie.surge.data.common.utils.IpAddressUtils;
import io.shulie.surge.data.common.zk.ZkClient;
import io.shulie.takin.cloud.biz.output.engine.EngineLogPtlConfigOutput;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.common.constants.ZkNodePathConstants;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author moriarty
 */
@Service
@Slf4j
public class EngineConfigServiceImpl implements EngineConfigService {

    @Autowired
    private ZkClient zkClient;

    @Value("${surge.deploy.enableStart}")
    private boolean enableStartSurge;


    @Override
    public EngineLogPtlConfigOutput getEnginePtlConfig() {

        EngineLogPtlConfigOutput output = new EngineLogPtlConfigOutput();
        try {
            boolean exists = zkClient.exists(ZkNodePathConstants.PTL_ENABLE_PATH);
            if (!exists) {
                output.setPtlFileEnable(true);
                output.setLogCutOff(false);
                output.setPtlFileErrorOnly(false);
                output.setPtlFileTimeoutOnly(false);
                return output;
            }
            byte[] enableData = zkClient.getData(ZkNodePathConstants.PTL_ENABLE_PATH);
            String enableString = new String(enableData);
            if ("true".equals(enableString)) {
                output.setPtlFileEnable(true);
                if (!zkClient.exists(ZkNodePathConstants.PTL_ERROR_ONLY_PATH)
                        || "true".equals(new String(zkClient.getData(ZkNodePathConstants.PTL_ERROR_ONLY_PATH)))) {
                    output.setPtlFileErrorOnly(true);
                } else {
                    output.setPtlFileErrorOnly(false);
                }
                if (!zkClient.exists(ZkNodePathConstants.PTL_TIMEOUT_ONLY_PATH)
                        || "true".equals(new String(zkClient.getData(ZkNodePathConstants.PTL_TIMEOUT_ONLY_PATH)))) {
                    output.setPtlFileTimeoutOnly(true);
                    if (!zkClient.exists(ZkNodePathConstants.PTL_TIMEOUT_THRESHOLD_PATH)) {
                        output.setTimeoutThreshold(ZkNodePathConstants.DEFAULT_TIMEOUT_THRESHOLD);
                    } else {
                        byte[] data = zkClient.getData(ZkNodePathConstants.PTL_TIMEOUT_THRESHOLD_PATH);
                        output.setTimeoutThreshold(Long.valueOf(new String(data)));
                    }
                } else {
                    output.setPtlFileTimeoutOnly(false);
                }
            }else {
                output.setPtlFileEnable(false);
            }
            if (!zkClient.exists(ZkNodePathConstants.PTL_LOG_CUTOFF_PATH)
                    || "false".equals(new String(zkClient.getData(ZkNodePathConstants.PTL_LOG_CUTOFF_PATH)))) {
                output.setLogCutOff(false);
            } else {
                output.setLogCutOff(true);
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：日志推送处理异常 --> 获取压测引擎日志配置参数异常: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR,e);
        }

        return output;
    }

    @Override
    public String getLogSimpling() {
        try {
            byte[] data = zkClient.getData(ZkNodePathConstants.LOG_SAMPLING_PATH);
            if (Objects.nonNull(data) && data.length > 0) {
                return new String(data);
            }
        } catch (Exception e) {
            return "1";
        }
        return "1";
    }

    @Override
    public String getLogPushServer(String failServer) {
        if (enableStartSurge) {
            return IpAddressUtils.getLocalAddress() + ":29900";
        }
        String serverAndPort = "";
        try {
            List<String> servers = zkClient.listChildren(ZkNodePathConstants.AMDB_LOG_UPLOAD_NODE_LIST_PATH);
            if (CollectionUtils.isNotEmpty(servers)) {
                for (String str : servers) {
                    if (StringUtils.isNotBlank(str) && !str.equals(failServer)) {
                        return str;
                    }
                }
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：推送日志，获取服务和端口异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR,e);
        }
        return serverAndPort;
    }

    /**
     * 获取需要挂载本地磁盘的场景ID
     *
     * @return
     */
    @Override
    public String[] getLocalMountSceneIds() {
        String result = "";
        try {
            byte[] data = zkClient.getData(ZkNodePathConstants.LOCAL_MOUNT_SCENE_IDS_PATH);
            if(Objects.nonNull(data) && data.length > 0) {
                result = new String(data);
            }
        } catch (Exception e) {
            return new String[]{};
        }
        if(StringUtils.isBlank(result)) {
            return new String[]{};
        }
        return result.trim().split(",");
    }

}
