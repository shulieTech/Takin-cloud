package io.shulie.takin.cloud.biz.service.statistics.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.cloudserver.StatisticsConverter;
import io.shulie.takin.cloud.biz.input.statistics.PressureTotalInput;
import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;
import io.shulie.takin.cloud.biz.service.statistics.PressureStatisticsService;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.data.dao.statistics.StatisticsManageDao;
import io.shulie.takin.cloud.data.result.statistics.PressureListTotalResult;
import io.shulie.takin.cloud.data.result.statistics.PressurePieTotalResult;
import io.shulie.takin.cloud.data.result.statistics.ReportTotalResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/11/30 9:36 下午
 */
@Service
public class PressureStatisticsServiceImpl implements PressureStatisticsService {

    @Autowired
    private StatisticsManageDao statisticsManageDao;

    @Override
    public PressurePieTotalOutput getPressurePieTotal(PressureTotalInput input) {
        List<PressurePieTotalResult> list = statisticsManageDao.getPressureScenePieTotal(input.getStartTime(),
            input.getEndTime());
        List<PressurePieTotalOutput.PressurePieTotal> totals = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            list.stream().map(data -> {
                    PressurePieTotalOutput.PressurePieTotal total = new PressurePieTotalOutput.PressurePieTotal();
                    total.setType(
                        SceneManageStatusEnum.getSceneManageStatusEnum(SceneManageStatusEnum.getAdaptStatus(data.getStatus()))
                            .getDesc());
                    total.setValue(data.getCount());
                    return total;
                }).collect(Collectors.groupingBy(PressurePieTotalOutput.PressurePieTotal::getType))
                .forEach((k, v) -> {
                    Optional<PressurePieTotalOutput.PressurePieTotal> sum = v.stream().reduce((v1, v2) -> {
                        //合并
                        v1.setValue(v1.getValue() + v2.getValue());
                        return v1;
                    });
                    totals.add(sum.orElse(new PressurePieTotalOutput.PressurePieTotal()));
                });
        }
        // 判断下是否有压测中
        if (totals.stream().noneMatch(total -> total.getType().equals(SceneManageStatusEnum.PRESSURE_TESTING.getDesc()))) {
            PressurePieTotalOutput.PressurePieTotal pieTotal = new PressurePieTotalOutput.PressurePieTotal();
            pieTotal.setValue(0);
            pieTotal.setType(SceneManageStatusEnum.PRESSURE_TESTING.getDesc());
            totals.add(pieTotal);
        }
        // 判断是否有待启动
        if (totals.stream().noneMatch(total -> total.getType().equals(SceneManageStatusEnum.WAIT.getDesc()))) {
            PressurePieTotalOutput.PressurePieTotal pieTotal = new PressurePieTotalOutput.PressurePieTotal();
            pieTotal.setValue(0);
            pieTotal.setType(SceneManageStatusEnum.WAIT.getDesc());
            totals.add(pieTotal);
        }
        Integer count = list.stream().mapToInt(PressurePieTotalResult::getCount).sum();
        PressurePieTotalOutput result = new PressurePieTotalOutput();
        result.setData(totals);
        result.setTotal(Integer.parseInt(String.valueOf(count)));
        return result;
    }

    @Override
    public ReportTotalOutput getReportTotal(PressureTotalInput input) {
        // 需要先统计这个时间内创建的场景
        ReportTotalResult result = statisticsManageDao.getReportTotal(input.getStartTime(), input.getEndTime());
        ReportTotalOutput output = new ReportTotalOutput();
        BeanUtils.copyProperties(result, output);
        return output;
    }

    @Override
    public List<PressureListTotalOutput> getPressureListTotal(PressureTotalInput input) {
        List<PressureListTotalResult> list = Lists.newArrayList();
        switch (input.getType()) {
            case "0":
                list = statisticsManageDao.getPressureSceneListTotal(input.getStartTime(), input.getEndTime());
                break;
            case "1":
                if (input.getScriptIds() != null && input.getScriptIds().size() > 0) {
                    list = statisticsManageDao.getPressureScriptListTotal(input.getStartTime(), input.getEndTime(), input.getScriptIds());
                }
                break;
            default: {}
        }

        return StatisticsConverter.INSTANCE.ofResult(list);
    }

}
