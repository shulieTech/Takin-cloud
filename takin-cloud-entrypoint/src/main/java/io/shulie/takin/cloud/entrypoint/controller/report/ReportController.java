package io.shulie.takin.cloud.entrypoint.controller.report;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.sdk.model.request.WarnQueryParam;
import com.pamirs.takin.entity.domain.vo.report.ReportIdParam;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.entrypoint.convert.WarnDetailRespConvertor;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@RestController

@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_REPORT)
@Api(tags = "场景报告模块", value = "场景报告")
public class ReportController {

    @Resource
    private ReportService reportService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation("报告列表")
    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST)
    public ResponseResult<List<CloudReportDTO>> listReport(ReportQueryReq reportQuery) {
        PageInfo<CloudReportDTO> reportList = reportService.listReport(reportQuery);
        return ResponseResult.success(reportList.getList(), reportList.getTotal());
    }

    /**
     * 迁移到open-opi
     *
     * @param reportId 报告主键
     * @return -
     */
    @Deprecated
    @ApiOperation("报告详情")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    @GetMapping(value = EntrypointUrl.METHOD_REPORT_DETAIL)
    public ResponseResult<ReportDetailResp> getReportByReportId(Long reportId) {
        ReportDetailOutput detailOutput = reportService.getReportByReportId(reportId);
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在Id:" + reportId);
        }
        return ResponseResult.success(BeanUtil.copyProperties(detailOutput, ReportDetailResp.class));
    }

    /**
     * 缓存报告链路数据
     *
     * @param reportTrendQuery 请求数据
     * @return -
     */
    @ApiOperation("报告链路趋势")
    @GetMapping(EntrypointUrl.METHOD_REPORT_TREND)
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq reportTrendQuery) {
        try {
            String key = JSON.toJSONString(reportTrendQuery);
            ReportTrendResp data;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                data = JSON.parseObject(redisTemplate.opsForValue().get(key), ReportTrendResp.class);
                if (Objects.isNull(data)
                    || CollectionUtils.isEmpty(data.getConcurrent())
                    || CollectionUtils.isEmpty(data.getSa())
                    || CollectionUtils.isEmpty(data.getRt())
                    || CollectionUtils.isEmpty(data.getTps())
                    || CollectionUtils.isEmpty(data.getSuccessRate())) {
                    data = reportService.queryReportTrend(reportTrendQuery);
                    redisTemplate.opsForValue().set(key, JSON.toJSONString(data));
                }
            } else {
                data = reportService.queryReportTrend(reportTrendQuery);
                redisTemplate.opsForValue().set(key, JSON.toJSONString(data));
            }
            return ResponseResult.success(data);
        } catch (Exception e) {
            return ResponseResult.success(new ReportTrendResp());
        }
    }

    /**
     * 实况报表
     * 迁移到open-api
     */
    @Deprecated
    @ApiOperation("实况报告")
    @GetMapping(EntrypointUrl.METHOD_REPORT_DETAIL_TEMP)
    @ApiImplicitParam(name = "sceneId", value = "场景ID")
    public ResponseResult<ReportDetailResp> tempReportDetail(Long sceneId) {
        ReportDetailOutput detailOutput = reportService.tempReportDetail(sceneId);
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在");
        }
        ReportDetailResp resp = new ReportDetailResp();
        BeanUtils.copyProperties(detailOutput, resp);
        if (CollectionUtils.isNotEmpty(detailOutput.getStopReasons())) {
            resp.setStopReasons(detailOutput.getStopReasons());
        }
        return ResponseResult.success(resp);
    }

    @ApiOperation("实况报告链路趋势")
    @GetMapping(EntrypointUrl.METHOD_REPORT_TREND_TEMP)
    public ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryReq reportTrendQuery) {
        ReportTrendResp data = reportService.queryTempReportTrend(reportTrendQuery);
        return ResponseResult.success(data);
    }

    @ApiOperation("警告列表")
    @GetMapping(EntrypointUrl.METHOD_REPORT_WARN_LIST)
    public ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryParam param) {
        PageInfo<WarnDetailOutput> list = reportService.listWarn(param);
        List<WarnDetailResponse> responses = WarnDetailRespConvertor.INSTANCE.ofList(list.getList());
        return ResponseResult.success(responses, list.getTotal());
    }

    @ApiOperation("报告的业务活动")
    @GetMapping(EntrypointUrl.METHOD_REPORT_ACTIVITY_REPORT_ID)
    public ResponseResult<List<BusinessActivityDTO>> queryReportActivityByReportId(Long reportId) {
        List<BusinessActivityDTO> data = reportService.queryReportActivityByReportId(reportId);
        return ResponseResult.success(data);
    }

    @ApiOperation("报告的业务活动")
    @GetMapping(EntrypointUrl.METHOD_REPORT_ACTIVITY_SCENE_ID)
    public ResponseResult<List<BusinessActivityDTO>> queryReportActivityBySceneId(Long sceneId) {
        List<BusinessActivityDTO> data = reportService.queryReportActivityBySceneId(sceneId);
        return ResponseResult.success(data);
    }

    /**
     * 压测明细
     */
    @ApiOperation("压测明细")
    @GetMapping(EntrypointUrl.METHOD_REPORT_SUMMARY)
    public ResponseResult<NodeTreeSummaryResp> queryActivitiesSummaryList(ReportDetailByIdReq req) {
        return ResponseResult.success(reportService.getNodeSummaryList(req.getReportId()));
    }

    @ApiOperation("报告汇总")
    @GetMapping(EntrypointUrl.METHOD_REPORT_WARN_COUNT)
    public ResponseResult<Map<String, Object>> getReportWarnCount(Long reportId) {
        return ResponseResult.success(reportService.getReportWarnCount(reportId));
    }

    @ApiOperation("查询正在生成的报告")
    @GetMapping(EntrypointUrl.METHOD_REPORT_ONE_RUNNING)
    public ResponseResult<Long> queryRunningReport(ContextExt contextExt) {
        return ResponseResult.success(reportService.queryRunningReport(contextExt));
    }

    @Deprecated
    @ApiOperation("查询正在生成的报告列表")
    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST_RUNNING)
    public ResponseResult<List<Long>> queryListRunningReport() {
        return ResponseResult.success(reportService.queryListRunningReport());
    }

    @ApiOperation("查询正在压测的报告列表")
    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST_PRESSURING)
    public ResponseResult<List<Long>> queryListPressuringReport() {
        return ResponseResult.success(reportService.queryListPressuringReport());
    }

    @ApiOperation("锁定报告")
    @PutMapping(EntrypointUrl.METHOD_REPORT_LOCK)
    public ResponseResult<Boolean> lockReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.lockReport(param.getReportId()));
    }

    @ApiOperation("解锁报告")
    @PutMapping(EntrypointUrl.METHOD_REPORT_UNLOCK)
    public ResponseResult<Boolean> unLockReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.unLockReport(param.getReportId()));
    }

    @ApiOperation("报告结束")
    @PutMapping(EntrypointUrl.METHOD_REPORT_FINISH)
    public ResponseResult<Boolean> finishReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.finishReport(param.getReportId()));
    }

    @ApiOperation("当前压测的所有数据")
    @GetMapping(EntrypointUrl.METHOD_REPORT_METRICS)
    public ResponseResult<List<Map<String, Object>>> metrics(Long reportId, Long sceneId) {
        return ResponseResult.success(reportService.metric(reportId, sceneId));
    }

    @ApiOperation("创建告警")
    @PostMapping(EntrypointUrl.METHOD_REPORT_WARN_ADD)
    public ResponseResult<String> addWarn(@RequestBody WarnCreateReq req) {
        WarnCreateInput input = new WarnCreateInput();
        BeanUtils.copyProperties(req, input);
        reportService.addWarn(input);
        return ResponseResult.success("创建告警成功");
    }

    @ApiOperation("场景对应的脚本树结构")
    @GetMapping(EntrypointUrl.METHOD_REPORT_SCRIPT_NODE_TREE)
    public ResponseResult<List<ScriptNodeTreeResp>> getNodeTree(ScriptNodeTreeQueryReq req) {
        return ResponseResult.success(reportService.getNodeTree(req));
    }

    /**
     * 更新报告
     */
    @ApiOperation("更新报告-漏数检查使用")
    @PutMapping(EntrypointUrl.METHOD_REPORT_UPDATE_CONCLUSION)
    public ResponseResult<String> updateReportConclusion(@RequestBody UpdateReportConclusionReq req) {
        UpdateReportConclusionInput input = new UpdateReportConclusionInput();
        BeanUtils.copyProperties(req, input);
        reportService.updateReportConclusion(input);
        return ResponseResult.success("更新成功");
    }

}
