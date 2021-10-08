package io.shulie.takin.cloud.biz.service.log.impl;

import com.pamirs.pradar.log.parser.DataType;
import com.pamirs.pradar.log.parser.packet.Request;
import com.pamirs.pradar.remoting.RemotingClient;
import com.pamirs.pradar.remoting.protocol.*;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.biz.service.log.PushLogService;
import io.shulie.takin.cloud.biz.utils.PradarCoreUtils;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 日志推送 service - 实现
 *
 * @author -
 */
@Slf4j
@Service
public class PushLogServiceImpl implements PushLogService {

    @Autowired
    private RemotingClient remotingClient;

    @Autowired
    private ProtocolFactorySelector selector;

    @Autowired
    private EngineConfigService engineConfigService;

    @Override
    public void pushLogToAmdb(byte[] data, String version) {
        String serverAndPort = engineConfigService.getLogPushServer(null);
        boolean b = logPush(data, version, serverAndPort);
        int retryTimes = 3;
        while (!b && retryTimes > 0) {
            serverAndPort = engineConfigService.getLogPushServer(serverAndPort);
            b = logPush(data, version, serverAndPort);
            retryTimes--;
        }
    }

    @Override
    public void pushLogToAmdb(String context, String version) {
        String serverAndPort = engineConfigService.getLogPushServer(null);
        logPush(context.getBytes(StandardCharsets.UTF_8), version, serverAndPort);
    }

    private boolean logPush(byte[] data, String version, String serverAndPort) {
        if (StringUtils.isBlank(serverAndPort)) {
            log.error("不存在有效的服务节点");
            return false;
        }
        try {
            RemotingCommand command = new RemotingCommand();
            command.setCode(CommandCode.SUCCESS);
            command.setVersion(CommandVersion.V1);
            command.setProtocolCode(ProtocolCode.JAVA);
            Request request = new Request();
            request.setDataType(DataType.TRACE_LOG);
            request.setBody(data);
            request.setVersion(version);
            request.setHostIp(PradarCoreUtils.getLocalAddress());
            request.setCharset(PradarCoreUtils.DEFAULT_CHARSET.name());
            ProtocolFactory factory = selector.select(command.getProtocolCode());
            factory.encode(request, command);
            RemotingCommand responseCommand = remotingClient.invokeSync(serverAndPort, command, 1000 * 5);
            if (responseCommand.getCode() == CommandCode.SUCCESS) {
                log.debug("日志上传成功..{}", System.currentTimeMillis());
                return true;
            } else if (responseCommand.getCode() == CommandCode.SYSTEM_ERROR) {
                log.error("异常代码【{}】,异常内容：日志推送处理异常 --> 日志上传失败: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, new String(responseCommand.getBody()));
                return false;
            }
        } catch (Throwable e) {
            log.error("异常代码【{}】,异常内容：日志推送处理异常 --> 日志上传失败: {}",
                TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, e);
            return false;
        }
        return true;
    }

}
