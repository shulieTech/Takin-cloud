package io.shulie.takin.cloud.web.entrypoint.controller.schedule;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.schedule.ScheduleRecordDTO;
import com.pamirs.takin.entity.domain.vo.schedule.ScheduleRecordQueryVO;
import io.shulie.takin.cloud.biz.cache.DictionaryCache;
import io.shulie.takin.cloud.biz.service.record.ScheduleRecordService;
import io.shulie.takin.cloud.common.constants.DicKeyConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ScheduleRecordController
 * @Description
 * @Author qianshui
 * @Date 2020/5/9 下午2:05
 */
@RestController
@RequestMapping("/api/schedulerecord")
@Api(tags = "调度记录")
public class ScheduleRecordController {

    @Autowired
    private ScheduleRecordService scheduleRecordService;

    @Autowired
    private DictionaryCache dictionaryCache;

    @GetMapping("/list")
    @ApiOperation(value = "调度记录列表")
    public ResponseResult<List<ScheduleRecordDTO>> getList(
        @ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "sceneId", value = "压测场景ID", required = true) Long sceneId) {

        /**
         * 1、封装参数
         * 2、调用查询服务
         * 3、返回指定格式
         */
        if (sceneId == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCHEDULE_RECORD_GET_ERROR,"缺少压测场景ID");
        }
        ScheduleRecordQueryVO queryVO = new ScheduleRecordQueryVO();
        queryVO.setCurrentPage(current);
        queryVO.setPageSize(pageSize);
        queryVO.setSceneId(sceneId);
        PageInfo<ScheduleRecordDTO> pageInfo = scheduleRecordService.queryPageList(queryVO);
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(data -> data
                .setStatus(dictionaryCache.getObjectByParam(DicKeyConstant.SUCCESS_STATUS, data.getStatusInt())));
        }
        return ResponseResult.success(pageInfo.getList());
    }
}
