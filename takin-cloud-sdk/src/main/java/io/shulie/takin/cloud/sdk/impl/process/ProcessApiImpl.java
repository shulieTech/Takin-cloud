package io.shulie.takin.cloud.sdk.impl.process;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import org.springframework.stereotype.Service;

import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.entrypoint.process.ProcessApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptAnalyzeRequest;

/**
 * 新业务流程相关接口
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ProcessApiImpl implements ProcessApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    /**
     * 脚本解析
     *
     * @param request 入参
     * @return 脚本解析结果
     */
    @Override
    public List<ScriptNode> scriptAnalyze(ScriptAnalyzeRequest request) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_PROCESS, EntrypointUrl.METHOD_PROCESS_SCRIPT_ANALYZE),
            request, new TypeReference<ResponseResult<List<ScriptNode>>>() {}).getData();
    }
}
