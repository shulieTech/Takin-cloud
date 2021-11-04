package io.shulie.takin.cloud.web.entrypoint.controller.report;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import io.swagger.annotations.Api;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.shulie.takin.cloud.common.constants.APIUrls;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
import io.shulie.takin.cloud.web.entrypoint.convert.WarnDetailRespConvertor;
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.web.entrypoint.response.scenemanage.WarnDetailResponse;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL)
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

    @GetMapping("report/listReport")
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
    @GetMapping(value = "report/getReportByReportId")
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
    @GetMapping("report/queryReportTrend")
    @ApiOperation("报告链路趋势")
    public ResponseResult<ReportTrendDTO> queryReportTrend(ReportTrendQueryParam reportTrendQuery) {
        try {
            String key = JSON.toJSONString(reportTrendQuery);
            ReportTrendDTO data;
            if (redisClientUtils.hasKey(key)) {
                data = JSON.parseObject(redisClientUtils.getString(key), ReportTrendDTO.class);
                if (Objects.isNull(data) ||CollectionUtils.isEmpty(data.getConcurrent()) || CollectionUtils.isEmpty(data.getSa())
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
    @GetMapping("report/tempReportDetail")
    @ApiOperation("实况报告")
    @ApiImplicitParam(name = "sceneId", value = "场景ID")
    public ResponseResult<ReportDetailOutput> tempReportDetail(Long sceneId) {
        ReportDetailOutput detail = reportService.tempReportDetail(sceneId);
        if (detail == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "实况报表不存在");
        }
        return ResponseResult.success(detail);
    }

    @GetMapping("/report/queryTempReportTrend")
    @ApiOperation("实况报告链路趋势")
    public ResponseResult<ReportTrendDTO> queryTempReportTrend(ReportTrendQueryParam reportTrendQuery) {
        ReportTrendDTO data = reportService.queryTempReportTrend(reportTrendQuery);
        return ResponseResult.success(data);
    }

    @GetMapping("/report/listWarn")
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

    @GetMapping("/report/queryReportActivityByReportId")
    @ApiOperation("报告的业务活动")
    public ResponseResult<List<BusinessActivityDTO>> queryReportActivityByReportId(Long reportId) {
        List<BusinessActivityDTO> data = reportService.queryReportActivityByReportId(reportId);
        return ResponseResult.success(data);
    }

    @GetMapping("/report/queryReportActivityBySceneId")
    @ApiOperation("报告的业务活动")
    public ResponseResult<List<BusinessActivityDTO>> queryReportActivityBySceneId(Long sceneId) {
        List<BusinessActivityDTO> data = reportService.queryReportActivityBySceneId(sceneId);
        return ResponseResult.success(data);
    }

    @GetMapping("/report/businessActivity/summary/list")
    @ApiOperation("压测明细")
    public ResponseResult<List<BusinessActivitySummaryBean>> getBusinessActivitySummaryList(Long reportId) {
        return ResponseResult.success(reportService.getBusinessActivitySummaryList(reportId));
    }

    @GetMapping("/report/count")
    @ApiOperation("报告汇总")
    public ResponseResult<Map<String, Object>> getReportCount(Long reportId) {
        return ResponseResult.success(reportService.getReportCount(reportId));
    }

    @GetMapping("/report/running")
    @ApiOperation("查询正在生成的报告")
    public ResponseResult<Long> queryRunningReport() {
        return ResponseResult.success(reportService.queryRunningReport());
    }

    @GetMapping("/report/running/list")
    @ApiOperation("查询正在生成的报告列表")
    public ResponseResult<List<Long>> queryListRunningReport() {
        return ResponseResult.success(reportService.queryListRunningReport());
    }

    @PutMapping("/report/lock")
    @ApiOperation("锁定报告")
    public ResponseResult<Boolean> lockReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.lockReport(param.getReportId()));
    }

    @PutMapping("/report/unlock")
    @ApiOperation("解锁报告")
    public ResponseResult<Boolean> unLockReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.unLockReport(param.getReportId()));
    }

    @PutMapping("/report/finish")
    @ApiOperation("报告结束")
    public ResponseResult<Boolean> finishReport(@RequestBody ReportIdParam param) {
        return ResponseResult.success(reportService.finishReport(param.getReportId()));
    }

    @GetMapping("/report/metrices")
    @ApiOperation("当前压测的所有数据")
    public ResponseResult<List<Metrices>> metrics(Long reportId, Long sceneId, Long customerId) {
        return ResponseResult.success(reportService.metric(reportId, sceneId, customerId));
    }

    @GetMapping("/report/nodeTree")
    @ApiOperation("场景对应的脚本树结构")
    public ResponseResult<String> getNodeTree(Long sceneId){
        return ResponseResult.success(reportService.getNodeTree(sceneId));
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
}
