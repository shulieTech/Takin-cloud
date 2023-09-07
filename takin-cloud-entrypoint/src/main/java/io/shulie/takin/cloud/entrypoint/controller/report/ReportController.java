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
import com.pamirs.takin.entity.domain.dto.report.Metrices;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdsReq;
import io.shulie.takin.cloud.sdk.model.response.report.ReportActivityResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.model.ScriptNodeSummaryBean;
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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
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
@Slf4j
@RestController
@Api(tags = "场景报告模块", value = "场景报告")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_REPORT)
public class ReportController {

    @Resource
    private ReportService reportService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
        ReportDetailResp result = BeanUtil.copyProperties(detailOutput, ReportDetailResp.class);
        try {
            String jtlDownLoadUrl = reportService.getJtlDownLoadUrl(result.getId(), false);
            log.debug("获取报告详情时获取JTL下载路径:{}.", jtlDownLoadUrl);
            result.setHasJtl(true);
        } catch (Throwable e) {
            result.setHasJtl(false);
        }
        return ResponseResult.success(result);
    }

    /**
     * @param reportId 报告主键
     * @return -
     */
    @ApiOperation("报告状态查询")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    @GetMapping(value = EntrypointUrl.METHOD_REPORT_STATUS_BY_ID)
    public ResponseResult<Integer> getReportStatusById(Long reportId) {
        return ResponseResult.success(reportService.getReportStatusById(reportId));
    }

    /**
     * 解析数结构并获取ApplicationIds字段
     *
     * @param resource       树结构
     * @param applicationIds 树中关联的所有业务活动对应的业务活动主键
     *                       <p>结果类似于:["1,2,3","2,3","1,3"]</p>
     */
    public void fillApplicationIds(List<ScriptNodeSummaryBean> resource, List<String> applicationIds) {
        if (resource != null && resource.size() > 0) {
            for (ScriptNodeSummaryBean item : resource) {
                applicationIds.add(item.getApplicationIds());
                fillApplicationIds(item.getChildren(), applicationIds);
            }
        }
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
            ReportTrendResp data = null;

            // 尝试从缓存获取数据
            if (stringRedisTemplate.hasKey(key)) {
                String cachedData = stringRedisTemplate.opsForValue().get(key);
                if (cachedData != null) {
                    data = JSON.parseObject(cachedData, ReportTrendResp.class);
                }
            }
            // 如果缓存中数据为空或缓存不存在，重新查询并存储到缓存
            if (data == null
                    || CollectionUtils.isEmpty(data.getConcurrent())
                    || CollectionUtils.isEmpty(data.getSa())
                    || CollectionUtils.isEmpty(data.getRt())
                    || CollectionUtils.isEmpty(data.getTps())
                    || CollectionUtils.isEmpty(data.getSuccessRate())) {
                data = reportService.queryReportTrend(reportTrendQuery);
                stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(data));
            }
            return ResponseResult.success(data);
        } catch (Exception e) {
            // 记录异常信息
            log.error("Error while querying report trend.", e);
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
        ReportDetailResp resp = BeanUtil.copyProperties(detailOutput, ReportDetailResp.class);
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
    public ResponseResult<List<Metrices>> metrics(Long reportId, Long sceneId) {
        return ResponseResult.success(reportService.metric(reportId, sceneId));
    }

    @ApiOperation("创建告警")
    @PostMapping(EntrypointUrl.METHOD_REPORT_WARN_ADD)
    public ResponseResult<String> addWarn(@RequestBody WarnCreateReq req) {
        WarnCreateInput input = BeanUtil.copyProperties(req, WarnCreateInput.class);
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
        UpdateReportConclusionInput input = BeanUtil.copyProperties(req, UpdateReportConclusionInput.class);
        reportService.updateReportConclusion(input);
        return ResponseResult.success("更新成功");
    }

    /**
     * 获取下载jtl下载路径
     */
    @ApiOperation("获取下载jtl下载路径")
    @RequestMapping(EntrypointUrl.METHOD_REPORT_GET_JTL_DOWNLOAD_URL)
    public ResponseResult<String> getJtlDownLoadUrl(@ApiParam(name = "reportId", value = "报告id") Long reportId) {
        return ResponseResult.success(reportService.getJtlDownLoadUrl(reportId, true));
    }

    @ApiOperation("根据场景ID查询正在压测报告的业务活动信息")
    @PostMapping(EntrypointUrl.METHOD_REPORT_ACTIVITY_REPORT_IDS)
    public ResponseResult<List<ReportActivityResp>> queryReportActivity(@RequestBody ReportDetailByIdsReq req){
        return ResponseResult.success(reportService.getNodeDetailBySceneIds(req.getSceneIds()));
    }
}
