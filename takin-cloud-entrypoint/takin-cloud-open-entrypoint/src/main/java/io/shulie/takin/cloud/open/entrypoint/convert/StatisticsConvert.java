package io.shulie.takin.cloud.open.entrypoint.convert;

import java.util.List;
import java.util.stream.Collectors;

import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;
import io.shulie.takin.cloud.open.resp.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.ReportTotalResp;
import org.springframework.beans.BeanUtils;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.entrypoint.convert
 * @date 2020/12/1 7:14 下午
 */
public class StatisticsConvert {
    public static PressurePieTotalResp of(PressurePieTotalOutput output) {
        PressurePieTotalResp resp = new PressurePieTotalResp();
        BeanUtils.copyProperties(output,resp);
        return resp;
    }
    public static ReportTotalResp of(ReportTotalOutput output) {
        ReportTotalResp resp = new ReportTotalResp();
        BeanUtils.copyProperties(output,resp);
        return resp;
    }
    public static List<PressureListTotalResp> of(List<PressureListTotalOutput> output) {
        List<PressureListTotalResp> resps = output.stream().map(out -> {
            PressureListTotalResp resp = new PressureListTotalResp();
            BeanUtils.copyProperties(out,resp);
            return resp;
        }).collect(Collectors.toList());
        return resps;
    }
}
