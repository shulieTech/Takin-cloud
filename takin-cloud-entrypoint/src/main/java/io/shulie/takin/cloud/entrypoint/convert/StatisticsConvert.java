package io.shulie.takin.cloud.entrypoint.convert;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;
import io.shulie.takin.cloud.sdk.resp.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.sdk.resp.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.resp.statistics.ReportTotalResp;
import org.springframework.beans.BeanUtils;

/**
 * @author 无涯
 * @date 2020/12/1 7:14 下午
 */
public class StatisticsConvert {
    public static PressurePieTotalResp of(PressurePieTotalOutput output) {
        PressurePieTotalResp resp = new PressurePieTotalResp();
        BeanUtils.copyProperties(output, resp);
        return resp;
    }

    public static ReportTotalResp of(ReportTotalOutput output) {
        ReportTotalResp resp = new ReportTotalResp();
        BeanUtils.copyProperties(output, resp);
        return resp;
    }

    public static List<PressureListTotalResp> of(List<PressureListTotalOutput> output) {
        return output.stream()
            .map(out -> BeanUtil.copyProperties(out, PressureListTotalResp.class))
            .collect(Collectors.toList());
    }
}
