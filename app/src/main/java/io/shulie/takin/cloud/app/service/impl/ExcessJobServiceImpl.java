package io.shulie.takin.cloud.app.service.impl;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.Page;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.data.entity.JobEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.conf.WatchmanConfig;
import io.shulie.takin.cloud.model.callback.ExcessJob;
import io.shulie.takin.cloud.model.request.MetricsInfo;
import io.shulie.takin.cloud.app.service.MetricsService;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.data.entity.ExcessJobEntity;
import io.shulie.takin.cloud.constant.enums.ExcessJobType;
import io.shulie.takin.cloud.app.service.ExcessJobService;
import io.shulie.takin.cloud.data.entity.ExcessJobLogEntity;
import io.shulie.takin.cloud.data.service.ExcessJobMapperService;
import io.shulie.takin.cloud.data.service.ExcessJobLogMapperService;

/**
 * 定时任务 - 服务 - 实现
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "EXCESS-JOB")
public class ExcessJobServiceImpl implements ExcessJobService {
    @Resource
    WatchmanConfig watchmanConfig;
    @Resource
    JobService jobService;
    @Resource
    JsonServiceImpl jsonService;
    @Resource
    MetricsService metricsService;
    @Resource
    CallbackService callbackService;
    @Resource(name = "excessJobMapperServiceImpl")
    ExcessJobMapperService excessJobMapper;
    @Resource(name = "excessJobLogMapperServiceImpl")
    ExcessJobLogMapperService excessJobLogMapper;

    @Override
    public PageInfo<ExcessJobEntity> list(int pageNumber, int pageSize, Integer type, boolean isCompleted) {
        try (Page<Object> ignore = PageMethod.startPage(pageNumber, pageSize)) {
            List<ExcessJobEntity> list = excessJobMapper.lambdaQuery()
                // 类型筛选
                .eq(type != null, ExcessJobEntity::getType, type)
                // 未完成
                .eq(ExcessJobEntity::getCompleted, isCompleted)
                // 并且
                .and(t ->
                    // (阈值时间为空 || 阈值时间小于等于当前时间)
                    t.isNull(ExcessJobEntity::getThresholdTime)
                        .or(c -> c.le(ExcessJobEntity::getThresholdTime, new Date())))
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
        boolean saveResult = excessJobMapper.save(excessJobEntity);
        return saveResult ? excessJobEntity.getId() : null;
    }

    @Override
    public Long log(long excessJobId, String content, boolean isCompleted) {
        ExcessJobLogEntity excessJobLogEntity = new ExcessJobLogEntity()
            .setExcessJobId(excessJobId)
            .setCompleted(isCompleted)
            .setContent(content);
        boolean saveResult = excessJobLogMapper.save(excessJobLogEntity);
        if (saveResult && isCompleted) {
            // 更新任务信息
            excessJobMapper.lambdaUpdate()
                .eq(ExcessJobEntity::getId, excessJobId)
                .set(ExcessJobEntity::getCompleted, excessJobLogEntity.getCompleted())
                .update();
            return excessJobLogEntity.getId();
        } else {
            updateThresholdTime(excessJobId);
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
                    execContent = execDataCalibrationFileList(entity.getJobId());
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
            log.error("单次执行失败\n{}\n", entity, ex);
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
                callbackService.callback(null, jobEntity.getCallbackUrl(), jsonService.writeValueAsString(excessJob));
            }
        }
    }

    @Override
    public boolean calibration(ExcessJobEntity entity) {
        Integer type = entity.getType();
        ExcessJobType excessJobType = ExcessJobType.of(type);
        String execContent = "";
        boolean completed = true;

        try {
            switch (excessJobType) {
                case DATA_CALIBRATION:
                    execContent = execDataCalibrationFileList(entity.getJobId());
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
            log.error("单次执行失败\n{}\n", entity, ex);
        } finally {
            // 记录执行信息
            log(entity.getId(), execContent, completed);
            // 组装回调信息
            JobEntity jobEntity = jobService.jobEntity(entity.getJobId());
            if (jobEntity == null) {
                log.warn(Message.MISS_JOB, entity.getJobId());
            } else {
                calibrationResultCallback(excessJobType, jobEntity, execContent, completed, entity);
            }
        }
        return false;
    }

    /**
     * 对校准结果进行回调
     *
     * @param excessJobType 额外任务类型
     * @param jobEntity     任务
     * @param execContent   执行结果
     * @param completed     是否完成
     * @param entity        额外任务
     */
    private void calibrationResultCallback(ExcessJobType excessJobType, JobEntity jobEntity, String execContent, boolean completed, ExcessJobEntity entity) {
        ExcessJob excessJob = new ExcessJob()
            .setContent(execContent)
            .setCompleted(completed)
            .setJobType(excessJobType)
            .setJobId(jobEntity.getId())
            .setResourceId(jobEntity.getResourceId());
        excessJob.setData(entity.getId());
        // 保存回调信息
        boolean complete = callbackService.callback(null, jobEntity.getCallbackUrl(), jsonService.writeValueAsString(excessJob));
        log.info("数据校准任务：{}, 回调结果: {}", entity.getJobId(), complete);
    }

    /**
     * 执行数据校正
     *
     * @param jobId 任务主键
     * @return 校正内容
     */
    private String execDataCalibrationFileList(long jobId) throws Exception {
        // 开始记时
        long startTime = System.nanoTime();
        // 获取任务实例
        JobEntity jobEntity = jobService.jobEntity(jobId);
        // 获取工作目录
        File directory = FileUtil.file(watchmanConfig.getNfsPath(), "metrics",
            String.valueOf(jobEntity.getResourceId()), String.valueOf(jobEntity.getId()));
        // 获取校正文件
        String[] directoryFileArray = directory.list((dir, name) -> Pattern.matches("^pressure-\\d\\.metrics\\.err$", name));
        long rollBackWriteLength = 0;
        if (directoryFileArray != null) {
            List<File> readyCalibrationFileList = Arrays.stream(directoryFileArray).map(t -> FileUtil.file(directory, t)).collect(Collectors.toList());
            //校验文件大小
            readyCalibrationFileList = readyCalibrationFileList.stream().filter(file -> FileUtil.size(file) > 0).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(readyCalibrationFileList)) {
                // 获取回滚文件
                File rollBackFile = getRollBackFile(directory);
                // 初始化回滚并启动
                initRollBack(jobId, rollBackFile, readyCalibrationFileList);
                // 判断回滚文件是否包含数据
                rollBackWriteLength = rollBackFile.length();
            }
        }

        // 记录状态信息
        String message = CharSequenceUtil.format("同步任务{}.耗时:{}纳秒.结果:{}.", jobId, System.nanoTime() - startTime, rollBackWriteLength == 0);
        // 根据情况抛出异常/返回信息
        if (rollBackWriteLength == 0) {
            return message;
        } else {
            throw new IllegalStateException(message);
        }
    }

    /**
     * 执行数据校正 - 初始化回滚
     *
     * @param jobId        任务主键
     * @param rollBackFile 回滚文件
     * @param fifleList    文件列表
     */
    private void initRollBack(long jobId, File rollBackFile, List<File> fifleList) throws Exception {
        try (OutputStream rollBackOutputStream = Files.newOutputStream(rollBackFile.toPath())) {
            try (Writer rollBackWriter = new OutputStreamWriter(rollBackOutputStream)) {
                execDataCalibrationFileList(jobId, rollBackWriter, fifleList);
            }
        }
    }

    /**
     * 执行数据校正 - 所有文件
     *
     * @param jobId          任务主键
     * @param rollBackWriter 回滚文件的writer
     * @param fileList       文件列表
     */
    private void execDataCalibrationFileList(long jobId, Writer rollBackWriter, List<File> fileList) throws Exception {
        int fileLength = fileList.size();
        for (int i = 0; i < fileLength; i++) {
            File file = fileList.get(i);
            log.info("当前进度({}/{})", i + 1, fileLength);
            log.info("开始处理文件{}的内容入库", file.getAbsolutePath());
            try (InputStream in = Files.newInputStream(file.toPath())) {
                try (Reader inReader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufReader = new BufferedReader(inReader)) {
                        String line = "";
                        while (line != null) {
                            line = bufReader.readLine();
                            execDataCalibrationFileLine(jobId, rollBackWriter, line);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("处理文件{}时发生异常\n", file.getAbsolutePath(), e);
                throw e;
            }
        }
    }

    /**
     * 执行数据校正 - 文件行
     *
     * @param jobId          任务主键
     * @param rollBackWriter 回滚文件的writer
     * @param line           文件行内容
     * @throws IOException 文件IO异常
     */
    private void execDataCalibrationFileLine(long jobId, Writer rollBackWriter, String line) throws IOException {
        if (CharSequenceUtil.isNotBlank(line)) {
            List<MetricsInfo> metricsList = jsonService.readValue(line, new TypeReference<List<MetricsInfo>>() {
            });
            ArrayList<MetricsInfo> insertIntoInflux = new ArrayList<>(1);
            for (MetricsInfo metricsInfo : metricsList) {
                if ("response".equals(metricsInfo.getType())) {
                    insertIntoInflux.clear();
                    insertIntoInflux.add(metricsInfo);
                    execDataCalibrationItemToInflux(jobId, insertIntoInflux, rollBackWriter);
                }
            }
        }
    }

    /**
     * 执行数据校正 - 某一行 - 写入InfluxDB
     *
     * @param jobId          任务主键
     * @param data           要插入InfluxDB的数据集合
     * @param rollBackWriter 回滚文件的writer
     * @throws IOException 操作回滚的时候发生IO写入异常
     */

    void execDataCalibrationItemToInflux(long jobId, ArrayList<MetricsInfo> data, Writer rollBackWriter) throws IOException {
        try {
            metricsService.collectorToInfluxdb(jobId, data);
        } catch (Exception ex) {
            rollBackWriter.write(jsonService.writeValueAsString(data));
            rollBackWriter.write(System.lineSeparator());
            rollBackWriter.flush();
        }
    }

    /**
     * 获取回滚文件
     *
     * @param directory 文件生成所在的目录
     * @return 文件
     */
    File getRollBackFile(File directory) {
        int number = 1;
        String suffix = ".log";
        String prefix = "data_calibration-roll_back-";
        String regex = "^data_calibration-roll_back-\\d\\.log$";
        // 获取
        String[] directoryFileArray = directory.list((dir, name) -> Pattern.matches(regex, name));
        if (directoryFileArray != null) {
            number += directoryFileArray.length;
        }
        return FileUtil.file(directory, CharSequenceUtil.format("{}{}{}", prefix, number, suffix));
    }

    /**
     * 更新阈值时间
     *
     * @param excessJobId 额外任务主键
     */
    private void updateThresholdTime(long excessJobId) {
        try {
            // 参数校验
            ExcessJobEntity excessJobEntity = excessJobMapper.getById(excessJobId);
            if (excessJobEntity == null) {
                log.warn(Message.MISS_EXCESS_JOB, excessJobId);
                return;
            }
            // 获取错误次数
            Long logCount = excessJobLogMapper.lambdaQuery().eq(ExcessJobLogEntity::getExcessJobId, excessJobEntity.getId()).count();
            // 限定参与计算的最大错误次数
            logCount = logCount > 10 ? 10 : logCount;
            // 限定阈值时间
            Date baseTime = excessJobEntity.getThresholdTime() == null ? excessJobEntity.getCreateTime() : excessJobEntity.getThresholdTime();
            // 按秒累增
            DateTime thresholdTime = DateUtil.offsetSecond(baseTime, (int)(5 * logCount));
            // 更新数据库
            excessJobMapper.lambdaUpdate()
                .set(ExcessJobEntity::getThresholdTime, thresholdTime)
                .eq(ExcessJobEntity::getId, excessJobEntity.getId())
                .update();
        } catch (Exception e) {
            log.error("更新阈值时间失败:{}\n", excessJobId, e);
        }
    }
}
