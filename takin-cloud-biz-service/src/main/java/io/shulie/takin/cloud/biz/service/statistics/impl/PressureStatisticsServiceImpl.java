package io.shulie.takin.cloud.biz.service.statistics.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.input.statistics.PressureTotalInput;
import io.shulie.takin.cloud.data.dao.statistics.StatisticsManageDao;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.cloud.data.result.statistics.PressurePieTotalResult;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.data.result.statistics.PressureListTotalResult;
import io.shulie.takin.cloud.biz.service.statistics.PressureStatisticsService;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 无涯
 * @date 2020/11/30 9:36 下午
 */
@Service
public class PressureStatisticsServiceImpl implements PressureStatisticsService {

    @Autowired
    private StatisticsManageDao statisticsManageDao;

    @Override
    public PressurePieTotalResp getPressurePieTotal(PressureTotalInput input) {
        List<PressurePieTotalResult> list = statisticsManageDao.getPressureScenePieTotal(input.getStartTime(),
            input.getEndTime());
        List<PressurePieTotalResp.PressurePieTotal> totals = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            list.stream().map(data -> {
                    PressurePieTotalResp.PressurePieTotal total = new PressurePieTotalResp.PressurePieTotal();
                    total.setType(
                        SceneManageStatusEnum.getSceneManageStatusEnum(SceneManageStatusEnum.getAdaptStatus(data.getStatus()))
                            .getDesc());
                    total.setValue(data.getCount());
                    return total;
                }).collect(Collectors.groupingBy(PressurePieTotalResp.PressurePieTotal::getType))
                .forEach((k, v) -> {
                    Optional<PressurePieTotalResp.PressurePieTotal> sum = v.stream().reduce((v1, v2) -> {
                        //合并
                        v1.setValue(v1.getValue() + v2.getValue());
                        return v1;
                    });
                    totals.add(sum.orElse(new PressurePieTotalResp.PressurePieTotal()));
                });
        }
        // 判断下是否有压测中
        if (totals.stream().noneMatch(total -> total.getType().equals(SceneManageStatusEnum.PTING.getDesc()))) {
            PressurePieTotalResp.PressurePieTotal pieTotal = new PressurePieTotalResp.PressurePieTotal();
            pieTotal.setValue(0);
            pieTotal.setType(SceneManageStatusEnum.PTING.getDesc());
            totals.add(pieTotal);
        }
        // 判断是否有待启动
        if (totals.stream().noneMatch(total -> total.getType().equals(SceneManageStatusEnum.WAIT.getDesc()))) {
            PressurePieTotalResp.PressurePieTotal pieTotal = new PressurePieTotalResp.PressurePieTotal();
            pieTotal.setValue(0);
            pieTotal.setType(SceneManageStatusEnum.WAIT.getDesc());
            totals.add(pieTotal);
        }
        Integer count = list.stream().mapToInt(PressurePieTotalResult::getCount).sum();
        PressurePieTotalResp result = new PressurePieTotalResp();
        result.setData(totals);
        result.setTotal(Integer.parseInt(String.valueOf(count)));
        return result;
    }

    @Override
    public ReportTotalResp getReportTotal(PressureTotalInput input) {
        // 需要先统计这个时间内创建的场景
        return statisticsManageDao.getReportTotal(input.getStartTime(), input.getEndTime());

    }

    @Override
    public List<PressureListTotalResp> getPressureListTotal(PressureTotalInput input) {
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

        return list.stream().map(t -> BeanUtil.copyProperties(t, PressureListTotalResp.class))
            .collect(Collectors.toList());
    }

}
