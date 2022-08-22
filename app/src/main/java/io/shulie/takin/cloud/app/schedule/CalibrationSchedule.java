package io.shulie.takin.cloud.app.schedule;

import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;

import io.shulie.takin.cloud.data.entity.CalibrationEntity;
import io.shulie.takin.cloud.app.service.CalibrationService;

/**
 * 校准调度
 *
 * @author chenhongqiao@shulie.com
 */
@Slf4j(topic = "CALIBRATION")
public class CalibrationSchedule implements Runnable {

    private final CalibrationService calibrationService;

    public CalibrationSchedule(CalibrationService calibrationService) {
        this.calibrationService = calibrationService;
    }

    @Override
    public void run() {
        try {
            // 分页查询
            PageInfo<CalibrationEntity> ready = calibrationService.listNotCompleted(1, 10);
            log.info("开始调度.共{}条,本次计划调度{}条\n{}", ready.getTotal(), ready.getSize(), ready);
            for (int i = 0; i < ready.getSize(); i++) {
                CalibrationEntity entity = ready.getList().get(i);
                calibrationService.exec(entity);
            }
        } catch (RuntimeException e) {
            log.error("单次调度异常\n", e);
        }
    }
}
