package io.shulie.takin.cloud.app.schedule;

import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.data.entity.ExcessJobEntity;
import io.shulie.takin.cloud.app.service.ExcessJobService;

/**
 * 校准调度
 *
 * @author chenhongqiao@shulie.com
 */
@Slf4j
public class CalibrationSchedule implements Runnable {

    private final ExcessJobService excessJobService;

    public CalibrationSchedule(ExcessJobService excessJobService) {
        this.excessJobService = excessJobService;
    }

    @Override
    public void run() {
        // 分页查询
        PageInfo<ExcessJobEntity> ready = excessJobService.listNotCompleted(1, 10, null);
        log.info("开始调度.共{}条,本次计划调度{}条\n{}", ready.getTotal(), ready.getSize(), ready);
        for (int i = 0; i < ready.getSize(); i++) {
            ExcessJobEntity entity = ready.getList().get(i);
            excessJobService.calibration(entity);
        }
    }
}
