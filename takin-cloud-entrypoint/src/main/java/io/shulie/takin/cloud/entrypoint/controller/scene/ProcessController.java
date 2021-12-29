package io.shulie.takin.cloud.entrypoint.controller.scene;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import cn.hutool.core.io.FileUtil;
import io.shulie.takin.cloud.ext.api.EngineExtApi;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptAnalyzeRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务流程
 *
 * @author liyuanba
 * @date 2021/10/26 12:02 下午
 */
@RestController()
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_PROCESS)
@Api(tags = "新业务流程相关接口")
public class ProcessController {

    @Resource
    private EnginePluginUtils enginePluginUtils;

    @ApiOperation(value = "脚本解析")
    @PostMapping(EntrypointUrl.METHOD_PROCESS_SCRIPT_ANALYZE)
    public ResponseResult<List<ScriptNode>> scriptAnalyze(@RequestBody ScriptAnalyzeRequest request) {
        if (StringUtils.isBlank(request.getScriptFile())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_PARAMS_ERROR, "请提供脚本文件完整的路径和名称");
        }
        File file = FileUtil.file(request.getScriptFile());
        if (!file.exists() || !file.isFile()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_FILE_NOT_EXISTS, "请检测脚本文件是否存在");
        }
        EngineExtApi engineExtApi = enginePluginUtils.getEngineExtApi();
        List<ScriptNode> nodes = engineExtApi.buildNodeTree(request.getScriptFile());
        if (CollectionUtils.isEmpty(nodes)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_FAILED, "请检测脚本内容");
        }
        return ResponseResult.success(nodes);
    }
}
