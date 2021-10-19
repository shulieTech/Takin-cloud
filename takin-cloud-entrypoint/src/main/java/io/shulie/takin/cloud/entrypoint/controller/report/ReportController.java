package io.shulie.takin.cloud.entrypoint.controller.report;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.sdk.model.request.report.WarnCreateReq;
import io.swagger.annotations.Api;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.pamirs.takin.entity.domain.dto.report.Metrices;
import org.springframework.web.bind.annotation.RequestBody;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.common.bean.sla.WarnQueryParam;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import com.pamirs.takin.entity.domain.vo.report.ReportIdParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTrendDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import org.springframework.web.context.request.RequestContextHolder;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import org.springframework.web.context.request.ServletRequestAttributes;
import io.shulie.takin.cloud.entrypoint.convert.WarnDetailRespConvertor;
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;

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
    public ResponseResult<List<CloudReportDTO>> listReport(ReportQueryParam reportQuery) {
        PageInfo<CloudReportDTO> reportList = reportService.listReport(reportQuery);
        setHttpResponseHeader(reportList.getTotal());
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
    public ResponseResult<ReportDetailOutput> getReportByReportId(Long reportId) {
        ReportDetailOutput detail = reportService.getReportByReportId(reportId);
        if (detail == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在");
        }
        return ResponseResult.success(detail);
    }

    /**
     * 缓存报告链路数据
     *
     * @param reportTrendQuery 请求数据
     * @return -
     */
    @GetMapping(EntrypointUrl.METHOD_REPORT_TREND)
    @ApiOperation("报告链路趋势")
    public ResponseResult<ReportTrendDTO> queryReportTrend(ReportTrendQueryParam reportTrendQuery) {
        try {
            String key = JSON.toJSONString(reportTrendQuery);
            ReportTrendDTO data;
            if (redisClientUtils.hasKey(key)) {
                data = JSON.parseObject(redisClientUtils.getString(key), ReportTrendDTO.class);
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
            return ResponseResult.success(new ReportTrendDTO());
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
    public ResponseResult<ReportDetailOutput> tempReportDetail(Long sceneId) {
        ReportDetailOutput detail = reportService.tempReportDetail(sceneId);
        if (detail == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "实况报表不存在");
        }
        return ResponseResult.success(detail);
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_TREND_TEMP)
    @ApiOperation("实况报告链路趋势")
    public ResponseResult<ReportTrendDTO> queryTempReportTrend(ReportTrendQueryParam reportTrendQuery) {
        ReportTrendDTO data = reportService.queryTempReportTrend(reportTrendQuery);
        return ResponseResult.success(data);
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_WARN_LIST)
    @ApiOperation("警告列表")
    public ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryParam param) {
        PageInfo<WarnDetailOutput> warnDetailOutputPageInfo = reportService.listWarn(param);
        setHttpResponseHeader(warnDetailOutputPageInfo.getTotal());
        if (warnDetailOutputPageInfo.getTotal() == 0) {
            return ResponseResult.success(Lists.newArrayList());
        }
        List<WarnDetailResponse> responses = WarnDetailRespConvertor.INSTANCE.ofList(
            warnDetailOutputPageInfo.getList());
        return ResponseResult.success(responses);
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

    @GetMapping(EntrypointUrl.METHOD_REPORT_SUMMARY)
    @ApiOperation("压测明细")
    public ResponseResult<List<BusinessActivitySummaryBean>> getBusinessActivitySummaryList(Long reportId) {
        return ResponseResult.success(reportService.getBusinessActivitySummaryList(reportId));
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_COUNT)
    @ApiOperation("报告汇总")
    public ResponseResult<Map<String, Object>> getReportCount(Long reportId) {
        return ResponseResult.success(reportService.getReportCount(reportId));
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST_RUNNING)
    @ApiOperation("查询正在生成的报告")
    public ResponseResult<Long> queryRunningReport(ContextExt contextExt) {
        return ResponseResult.success(reportService.queryRunningReport(contextExt));
    }

    @GetMapping(EntrypointUrl.METHOD_REPORT_LIST_ID_RUNNING)
    @ApiOperation("查询正在生成的报告列表")
    @Deprecated
    public ResponseResult<List<Long>> queryListRunningReport(ContextExt contextExt) {
        return ResponseResult.success(reportService.queryListRunningReport(contextExt));
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
    public ResponseResult<List<Metrices>> metrics(Long reportId, Long sceneId, Long tenantId) {
        return ResponseResult.success(reportService.metric(reportId, sceneId, tenantId));
    }

    /**
     * todo 临时方案，后面逐渐去掉这种网络请求
     */
    private void setHttpResponseHeader(Long total) {
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
            .getResponse();
        response.setHeader("Access-Control-Expose-Headers", PAGE_TOTAL_HEADER);
        response.setHeader(PAGE_TOTAL_HEADER, total + "");
    }

    @PostMapping(EntrypointUrl.METHOD_REPORT_WARN_ADD)
    @ApiOperation("创建告警")
    public ResponseResult<String> addWarn(@RequestBody WarnCreateReq req) {
        WarnCreateInput input = new WarnCreateInput();
        BeanUtils.copyProperties(req, input);
        reportService.addWarn(input);
        return ResponseResult.success("创建告警成功");
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
}
