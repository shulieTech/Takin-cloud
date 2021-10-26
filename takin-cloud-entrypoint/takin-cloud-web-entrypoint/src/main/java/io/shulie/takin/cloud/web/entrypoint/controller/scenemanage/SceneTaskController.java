package io.shulie.takin.cloud.web.entrypoint.controller.scenemanage;

import com.pamirs.takin.entity.domain.vo.report.SceneTaskNotifyParam;
import com.pamirs.takin.entity.domain.vo.scenemanage.FileSplitResultVO;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneContactFileOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.ext.content.enginecall.ScheduleInitParamExt;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneActionOutput;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.biz.service.schedule.ScheduleService;
import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import io.shulie.takin.cloud.web.entrypoint.request.scenemanage.SceneManageIdRequest;
import io.shulie.takin.cloud.web.entrypoint.request.scenemanage.SceneTaskStartRequest;
import io.shulie.takin.cloud.web.entrypoint.response.SceneActionResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 莫问
 * @date 2020-04-27
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL + "scene/task/")
@Api(tags = "场景任务", value = "场景任务")
public class SceneTaskController {

    @Autowired
    private SceneTaskService sceneTaskService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FileSliceService fileSliceService;

    @PostMapping("/start")
    @ApiOperation(value = "开始场景测试")
    public ResponseResult<?> start(@RequestBody SceneTaskStartRequest request) {

        SceneTaskStartInput input = new SceneTaskStartInput();
        BeanUtils.copyProperties(request, input);
        return ResponseResult.success(sceneTaskService.start(input));
    }

    @PostMapping("/stop")
    @ApiOperation(value = "结束场景测试")
    public ResponseResult<?> stop(@RequestBody SceneManageIdRequest request) {
        sceneTaskService.stop(request.getId());
        return ResponseResult.success();
    }

    @GetMapping("/checkStartStatus")
    @ApiOperation(value = "检查启动状态")
    public ResponseResult<SceneActionResponse> checkStartStatus(Long sceneId) {
        SceneActionOutput sceneAction = sceneTaskService.checkSceneTaskStatus(sceneId, null);
        SceneActionResponse response = new SceneActionResponse();
        response.setData(sceneAction.getData());
        response.setMsg(sceneAction.getMsg());
        return ResponseResult.success(response);
    }

    @GetMapping("/taskResultNotify")
    @ApiOperation(value = "启动结果通知")
    public String taskResultNotify(SceneTaskNotifyParam notify) {
        return sceneTaskService.taskResultNotify(notify);
    }

    @GetMapping("/initCallback")
    @ApiOperation(value = "调度初始化回调函数")
    public ResponseResult<?> initCallback(ScheduleInitParamExt param) {
        // 初始化调度
        scheduleService.initScheduleCallback(param);
        return ResponseResult.success();
    }

    @PostMapping("/script/contactScene")
    @ApiModelProperty(value = "关联数据文件与场景,并对顺序分片的文件进行预分片")
    public ResponseResult contactScene(@RequestBody FileSplitResultVO resultVO){
        try {
            SceneContactFileOutput output = fileSliceService.contactScene(new SceneBigFileSliceParam() {{
                setFileName(resultVO.getFileName());
                setSceneId(resultVO.getSceneId());
                setIsSplit(resultVO.getIsSplit());
                setIsOrderSplit(resultVO.getIsOrderSplit());
            }});
            return  ResponseResult.success(output);
        }catch (TakinCloudException e){
            return ResponseResult.fail("关联文件与脚本、场景异常",e.getMessage());
        }
    }

}
