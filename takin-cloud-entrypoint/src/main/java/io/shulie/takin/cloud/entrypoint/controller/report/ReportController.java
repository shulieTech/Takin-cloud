package io.shulie.takin.cloud.entrypoint.controller.report;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.entity.domain.dto.report.Metrices;
import com.pamirs.takin.entity.domain.vo.report.ReportIdParam;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.entrypoint.convert.WarnDetailRespConvertor;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.ScriptNodeSummaryBean;
import io.shulie.takin.cloud.sdk.model.common.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.request.WarnQueryParam;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    /**
     * 原本调用方式，需要将token放入header中
     */
    public static final String PAGE_TOTAL_HEADER = "x-total-count";

    @Resource
    private ReportService reportService;

    @Resource
    private RedisClientUtils redisClientUtils;

    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST)
    @ApiOperation("报告列表")
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
    @GetMapping(value = EntrypointUrl.METHOD_REPORT_DETAIL)
    @ApiOperation("报告详情")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    public ResponseResult<ReportDetailResp> getReportByReportId(Long reportId) {
        ReportDetailOutput detailOutput = reportService.getReportByReportId(reportId);
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在Id:" + reportId);
        }
        ReportDetailResp resp = new ReportDetailResp();
        BeanUtils.copyProperties(detailOutput, resp);
        // 处理关联的应用主键，兼容web端定时任务
        {
            List<String> applicationIdStringList = new LinkedList<>();
            fillApplicationIds(detailOutput.getNodeDetail(), applicationIdStringList);
            Set<Long> applicationIdList = new LinkedHashSet<>();
            // 双循环转化id为一维数据
            applicationIdStringList.stream().filter(StrUtil::isNotBlank)
                .forEach(t -> Arrays.stream(t.split(",")).filter(StrUtil::isNotBlank)
                    .map(Long::parseLong).forEach(applicationIdList::add));
            // 填充响应对象的值
            resp.setBusinessActivity(new LinkedList<BusinessActivitySummaryBean>() {{
                add(new BusinessActivitySummaryBean() {{
                    setApplicationIds(applicationIdList.stream().map(String::valueOf).collect(Collectors.joining(",")));
                }});
            }});
        }
        return ResponseResult.success(resp);
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
    @GetMapping(EntrypointUrl.METHOD_REPORT_TREND)
    @ApiOperation("报告链路趋势")
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq reportTrendQuery) {
        try {
            String key = JSON.toJSONString(reportTrendQuery);
            ReportTrendResp data;
            if (redisClientUtils.hasKey(key)) {
                data = JSON.parseObject(redisClientUtils.getString(key), ReportTrendResp.class);
                if (Objects.isNull(data) || CollectionUtils.isEmpty(data.getConcurrent()) || CollectionUtils.isEmpty(data.getSa())
                    || CollectionUtils.isEmpty(data.getRt()) || CollectionUtils.isEmpty(data.getTps())
                    || CollectionUtils.isEmpty(data.getSuccessRate())) {
                    data = reportService.queryReportTrend(reportTrendQuery);
                    redisClientUtils.setString(key, JSON.toJSONString(data));
                }
            } else {
                data = reportService.queryReportTrend(reportTrendQuery);
                redisClientUtils.setString(key, JSON.toJSONString(data));
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
    @GetMapping(EntrypointUrl.METHOD_REPORT_DETAIL_TEMP)
    @ApiOperation("实况报告")
    @ApiImplicitParam(name = "sceneId", value = "场景ID")
    public ResponseResult<ReportDetailResp> tempReportDetail(Long sceneId) {
        ReportDetailOutput detailOutput = reportService.tempReportDetail(sceneId);
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在");
        }
        ReportDetailResp resp = new ReportDetailResp();
        BeanUtils.copyProperties(detailOutput, resp);
        return ResponseResult.success(resp);
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_TREND_TEMP)
    @ApiOperation("实况报告链路趋势")
    public ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryReq reportTrendQuery) {
        ReportTrendResp data = reportService.queryTempReportTrend(reportTrendQuery);
        return ResponseResult.success(data);
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_WARN_LIST)
    @ApiOperation("警告列表")
    public ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryParam param) {
        PageInfo<WarnDetailOutput> list = reportService.listWarn(param);
        List<WarnDetailResponse> responses = WarnDetailRespConvertor.INSTANCE.ofList(list.getList());
        return ResponseResult.success(responses, list.getTotal());
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_ACTIVITY_REPORT_ID)
    @ApiOperation("报告的业务活动")
    public ResponseResult<List<BusinessActivityDTO>> queryReportActivityByReportId(Long reportId) {
        List<BusinessActivityDTO> data = reportService.queryReportActivityByReportId(reportId);
        return ResponseResult.success(data);
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_ACTIVITY_SCENE_ID)
    @ApiOperation("报告的业务活动")
    public ResponseResult<List<BusinessActivityDTO>> queryReportActivityBySceneId(Long sceneId) {
        List<BusinessActivityDTO> data = reportService.queryReportActivityBySceneId(sceneId);
        return ResponseResult.success(data);
    }

    //@GetMapping(EntrypointUrl.METHOD_REPORT_SUMMARY)
    //@ApiOperation("压测明细")
    //public ResponseResult<NodeTreeSummaryResp> getBusinessActivitySummaryList(Long reportId) {
    //    return ResponseResult.success(reportService.getBusinessActivitySummaryList(reportId));
    //}

    @GetMapping(EntrypointUrl.METHOD_REPORT_WARN_COUNT)
    @ApiOperation("报告汇总")
    public ResponseResult<Map<String, Object>> getReportWarnCount(Long reportId) {
        return ResponseResult.success(reportService.getReportWarnCount(reportId));
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_ONE_RUNNING)
    @ApiOperation("查询正在生成的报告")
    public ResponseResult<Long> queryRunningReport(ContextExt contextExt) {
        return ResponseResult.success(reportService.queryRunningReport(contextExt));
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST_RUNNING)
    @ApiOperation("查询正在生成的报告列表")
    @Deprecated
    public ResponseResult<List<Long>> queryListRunningReport() {
        return ResponseResult.success(reportService.queryListRunningReport());
    }

    @GetMapping("/report/pressuring/list")
    @ApiOperation("查询正在压测的报告列表")
    public ResponseResult<List<Long>> queryListPressuringReport() {
        return ResponseResult.success(reportService.queryListPressuringReport());
    }

    @PutMapping(EntrypointUrl.METHOD_REPORT_LOCK)
    @ApiOperation("锁定报告")
    public ResponseResult<Boolean> lockReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.lockReport(param.getReportId()));
    }

    @PutMapping(EntrypointUrl.METHOD_REPORT_UNLOCK)
    @ApiOperation("解锁报告")
    public ResponseResult<Boolean> unLockReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.unLockReport(param.getReportId()));
    }

    @PutMapping(EntrypointUrl.METHOD_REPORT_FINISH)
    @ApiOperation("报告结束")
    public ResponseResult<Boolean> finishReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.finishReport(param.getReportId()));
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_METRICES)
    @ApiOperation("当前压测的所有数据")
    public ResponseResult<List<Metrices>> metrics(Long reportId, Long sceneId) {
        return ResponseResult.success(reportService.metric(reportId, sceneId));
    }

    @PostMapping(EntrypointUrl.METHOD_REPORT_WARN_ADD)
    @ApiOperation("创建告警")
    public ResponseResult<String> addWarn(@RequestBody WarnCreateReq req) {
        WarnCreateInput input = new WarnCreateInput();
        BeanUtils.copyProperties(req, input);
        reportService.addWarn(input);
        return ResponseResult.success("创建告警成功");
    }

    @GetMapping("/report/nodeTree")
    @ApiOperation("场景对应的脚本树结构")
    public ResponseResult<List<ScriptNodeTreeResp>> getNodeTree(ScriptNodeTreeQueryReq req) {
        return ResponseResult.success(reportService.getNodeTree(req));
    }

    /**
     * 更新报告
     */
    @PutMapping(EntrypointUrl.METHOD_REPORT_UPDATE_CONCLUSION)
    @ApiOperation("更新报告-漏数检查使用")
    public ResponseResult<String> updateReportConclusion(@RequestBody UpdateReportConclusionReq req) {
        UpdateReportConclusionInput input = new UpdateReportConclusionInput();
        BeanUtils.copyProperties(req, input);
        reportService.updateReportConclusion(input);
        return ResponseResult.success("更新成功");
    }

    /**
     * 节点树
     */
    @GetMapping("report/nodeTree")
    @ApiOperation("节点树")
    public ResponseResult<List<ScriptNodeTreeResp>> queryScriptNodeTree(ScriptNodeTreeQueryReq req) {
        List<ScriptNodeTreeResp> nodeTree = reportService.getNodeTree(req);
        return ResponseResult.success(nodeTree);
    }

    /**
     * 压测明细
     */
    @GetMapping("report/summary/list")
    @ApiOperation("压测明细")
    public ResponseResult<NodeTreeSummaryResp> queryActivitiesSummaryList(ReportDetailByIdReq req) {
        return ResponseResult.success(reportService.getNodeSummaryList(req.getReportId()));
    }

}
