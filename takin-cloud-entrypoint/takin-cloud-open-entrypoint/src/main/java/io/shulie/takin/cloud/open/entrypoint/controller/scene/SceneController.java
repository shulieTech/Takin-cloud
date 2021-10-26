package io.shulie.takin.cloud.open.entrypoint.controller.scene;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.api.EngineExtApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * 业务流程
 * @Author: liyuanba
 * @Date: 2021/10/26 12:02 下午
 */
@RestController
@RequestMapping(APIUrls.TRO_OPEN_API_URL + "scene")
@Api(tags = "新业务流程相关接口")
public class SceneController {

    @Autowired
    private EnginePluginUtils enginePluginUtils;

    @ApiOperation(value = "脚本解析")
    @PostMapping("/scriptAnalyze")
    @ResponseBody
    public ResponseResult<?> scriptAnalyze(ScriptAnalyzeRequest request) {
        if (StringUtils.isBlank(request.getScriptFile())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_PARAMS_ERROR, "请提供脚本文件完整的路径和名称");
        }
        File file = new File(request.getScriptFile());
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
