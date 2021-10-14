package io.shulie.takin.cloud.entrypoint.controller.statistics;

import java.util.List;

import io.shulie.takin.cloud.biz.input.statistics.PressureTotalInput;
import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;
import io.shulie.takin.cloud.biz.service.statistics.PressureStatisticsService;
import io.shulie.takin.cloud.common.constants.ApiUrls;
import io.shulie.takin.cloud.entrypoint.convert.StatisticsConvert;
import io.shulie.takin.cloud.sdk.req.statistics.PressureTotalReq;
import io.shulie.takin.cloud.sdk.resp.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.sdk.resp.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.resp.statistics.ReportTotalResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 无涯
 * @date 2020/11/30 7:07 下午
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_OPEN_API_URL + "/statistic")
public class PressureStatisticsOpenController {

    @Autowired
    private  PressureStatisticsService pressureStatisticsService;

    /**
     * 统计场景分类，返回饼状图数据

     * @return -
     */
    @GetMapping("/getPressurePieTotal")
    @ApiOperation("统计压测场景分类")
    public ResponseResult<PressurePieTotalResp> getPressurePieTotal(PressureTotalReq req) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(req.getStartTime());
        input.setEndTime(req.getEndTime());
        PressurePieTotalOutput output = pressureStatisticsService.getPressurePieTotal(input);
        return ResponseResult.success(StatisticsConvert.of(output));
    }

    /**
     * 统计报告通过/未通过

     * @return -
     */
    @GetMapping("/getReportTotal")
    @ApiOperation("统计报告通过以及未通过")
    public ResponseResult<ReportTotalResp> getReportTotal(PressureTotalReq req) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(req.getStartTime());
        input.setEndTime(req.getEndTime());
        ReportTotalOutput output = pressureStatisticsService.getReportTotal(input);
        return ResponseResult.success(StatisticsConvert.of(output));
    }

    /**
     * 压测场景次数统计 && 压测脚本次数统计

     * @return -
     */
    @PostMapping("/getPressureListTotal")
    @ApiOperation("统计压测场景次数以及压测脚本次数")
    public ResponseResult<List<PressureListTotalResp>> getPressureListTotal(@RequestBody PressureTotalReq req) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(req.getStartTime());
        input.setEndTime(req.getEndTime());
        input.setScriptIds(req.getScriptIds());
        input.setType(req.getType());
        List<PressureListTotalOutput> output = pressureStatisticsService.getPressureListTotal(input);
        return ResponseResult.success(StatisticsConvert.of(output));
    }

}
