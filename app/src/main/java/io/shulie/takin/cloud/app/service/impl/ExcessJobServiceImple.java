package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import io.shulie.takin.cloud.constant.enums.ExcessJobType;

import io.shulie.takin.cloud.app.entity.ExcessJobEntity;
import io.shulie.takin.cloud.app.service.ExcessJobService;
import io.shulie.takin.cloud.app.entity.ExcessJobLogEntity;
import io.shulie.takin.cloud.app.service.mapper.ExcessJobMapperService;
import io.shulie.takin.cloud.app.service.mapper.ExcessJobLogMapperService;

/**
 * 定时任务 - 服务 - 实现
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ExcessJobServiceImple implements ExcessJobService {
    @Resource
    ExcessJobMapperService excessJobMapperService;
    @Resource
    ExcessJobLogMapperService excessJobLogMapperService;

    @Override
    public PageInfo<ExcessJobEntity> list(int pageNumber, int pageSize, Integer type, boolean isCompleted) {
        try (Page<Object> ignore = PageMethod.startPage(pageNumber, pageNumber)) {
            List<ExcessJobEntity> list = excessJobMapperService.lambdaQuery()
                .eq(ExcessJobEntity::getCompleted, isCompleted)
                .eq(type != null, ExcessJobEntity::getType, type)
                .list();
            return PageInfo.of(list);
        }
    }

    @Override
    public Long create(int type, String content) {
        ExcessJobEntity excessJobEntity = new ExcessJobEntity()
            .setType(type)
            .setContent(content);
        boolean saveResult = excessJobMapperService.save(excessJobEntity);
        return saveResult ? excessJobEntity.getId() : null;
    }

    @Override
    public Long log(long scheduleId, String content, boolean isCompleted) {
        ExcessJobLogEntity excessJobLogEntity = new ExcessJobLogEntity()
            .setScheduleId(scheduleId)
            .setCompleted(isCompleted)
            .setContent(content);
        boolean saveResult = excessJobLogMapperService.save(excessJobLogEntity);
        if (saveResult && isCompleted) {
            // 更新任务信息
            excessJobMapperService.lambdaUpdate()
                .eq(ExcessJobEntity::getId, scheduleId)
                .set(ExcessJobEntity::getCompleted, excessJobLogEntity.getCompleted())
                .update();
            return excessJobLogEntity.getId();
        } else {
            return null;
        }
    }

    @Override
    public void exec(ExcessJobEntity entity) {
        Integer type = entity.getType();
        String content = entity.getContent();
        ExcessJobType excessJobType = ExcessJobType.of(type);
        String execContent = "";
        try {
            switch (excessJobType) {
                case DATA_CALIBRATION:
                    execContent = execDataCalibration(content);
                    break;
                case IGNORE:
                    execContent = "这是一个忽略项";
                    break;
                default:
                    throw new IllegalArgumentException("未知的调度类型:" + type);
            }
        } catch (Exception ex) {
            log(entity.getId(), ex.getMessage(), false);
        }
        log(entity.getId(), execContent, true);
    }

    private String execDataCalibration(String content) {
        long startTime = System.nanoTime();
        Long jobId = Long.valueOf(content);
        // TODO: 校正数据
        return CharSequenceUtil.format("同步任务{}.耗时:{}纳秒", jobId, System.nanoTime() - startTime);
    }

}
