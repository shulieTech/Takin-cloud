package io.shulie.takin.cloud.app.service.impl;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.conf.WatchmanConfig;
import io.shulie.takin.cloud.model.callback.ExcessJob;
import io.shulie.takin.cloud.app.entity.ExcessJobEntity;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.ExcessJobType;
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
@Slf4j(topic = "EXCESS-JOB")
public class ExcessJobServiceImple implements ExcessJobService {
    @Resource
    WatchmanConfig watchmanConfig;
    @Resource
    JobService jobService;
    @Resource
    JsonServiceImpl jsonService;
    @Resource
    CallbackService callbackService;
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
    public Long create(int type, long jobId, String content) {
        ExcessJobEntity excessJobEntity = new ExcessJobEntity()
            .setType(type)
            .setJobId(jobId)
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
        ExcessJobType excessJobType = ExcessJobType.of(type);
        String execContent = "";
        boolean completed = true;

        try {
            switch (excessJobType) {
                case DATA_CALIBRATION:
                    execContent = execDataCalibration(entity.getJobId());
                    break;
                case IGNORE:
                    execContent = "这是一个忽略项";
                    break;
                default:
                    throw new IllegalArgumentException("未知的调度类型:" + type);
            }
        } catch (Exception ex) {
            completed = false;
            execContent = ex.getMessage();
        } finally {
            // 记录执行信息
            log(entity.getId(), execContent, completed);
            // 组装回调信息
            JobEntity jobEntity = jobService.jobEntity(entity.getJobId());
            if (jobEntity == null) {
                log.warn(Message.MISS_JOB, entity.getJobId());
            } else {
                ExcessJob excessJob = new ExcessJob()
                    .setContent(execContent)
                    .setCompleted(completed)
                    .setJobType(excessJobType)
                    .setJobId(jobEntity.getId())
                    .setResourceId(jobEntity.getResourceId());
                excessJob.setData(entity.getId());
                // 保存回调信息
                callbackService.create(jobEntity.getCallbackUrl(), jsonService.writeValueAsString(excessJob));
            }
        }
    }

    private String execDataCalibration(long jobId) {
        long startTime = System.nanoTime();
        JobEntity jobEntity = jobService.jobEntity(jobId);
        File directory = FileUtil.file(watchmanConfig.getNfsPath(),
            String.valueOf(jobEntity.getResourceId()), String.valueOf(jobEntity.getId()));
        String[] readyCalibrationFileList = directory.list((dir, name) -> Pattern.matches("^pressure-\\d.metrics.err$", name));
        if (readyCalibrationFileList == null) {
            throw new IllegalArgumentException("目录不存在:" + directory);
        }
        for (int i = 0; i < readyCalibrationFileList.length; i++) {
            String filePath = readyCalibrationFileList[i];
            File file = new File(filePath);
            // TODO 文件内容入库
            log.info("当前进度({}/{})", i + 1, readyCalibrationFileList.length);
            log.info("开始处理文件{}的内容入库", file.getAbsolutePath());
        }
        return CharSequenceUtil.format("同步任务{}.耗时:{}纳秒", jobId, System.nanoTime() - startTime);
    }

}
