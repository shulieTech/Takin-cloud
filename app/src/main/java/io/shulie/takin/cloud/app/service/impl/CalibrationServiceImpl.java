package io.shulie.takin.cloud.app.service.impl;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
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
import io.shulie.takin.cloud.app.conf.WatchmanConfig;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureMetricsService;
import io.shulie.takin.cloud.model.callback.Calibration;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.data.entity.CalibrationEntity;
import io.shulie.takin.cloud.app.service.CalibrationService;
import io.shulie.takin.cloud.data.entity.CalibrationLogEntity;
import io.shulie.takin.cloud.data.service.CalibrationMapperService;
import io.shulie.takin.cloud.model.request.job.pressure.MetricsInfo;
import io.shulie.takin.cloud.data.service.CalibrationLogMapperService;

/**
 * 定时任务 - 服务 - 实现
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "CALIBRATION")
public class CalibrationServiceImpl implements CalibrationService {
    @Resource
    JsonServiceImpl jsonService;
    @Resource
    WatchmanConfig watchmanConfig;
    @Resource
    CallbackService callbackService;
    @Resource
    PressureService pressureService;
    @Resource
    PressureMetricsService pressureMetricsService;
    @Resource(name = "calibrationMapperServiceImpl")
    CalibrationMapperService calibrationMapper;
    @Resource(name = "calibrationLogMapperServiceImpl")
    CalibrationLogMapperService calibrationLogMapper;

    @Override
    public PageInfo<CalibrationEntity> list(int pageNumber, int pageSize, boolean isCompleted) {
        try (Page<Object> ignore = PageMethod.startPage(pageNumber, pageSize)) {
            List<CalibrationEntity> list = calibrationMapper.lambdaQuery()
                .eq(CalibrationEntity::getCompleted, isCompleted)
                .and(t ->
                    // (阈值时间为空 || 阈值时间小于等于当前时间)
                    t.isNull(CalibrationEntity::getThresholdTime)
                        .or(c -> c.le(CalibrationEntity::getThresholdTime, new Date())))
                .list();
            return PageInfo.of(list);
        }
    }

    @Override
    public Long create(long pressureId) {
        CalibrationEntity calibrationEntity = new CalibrationEntity().setPressureId(pressureId);
        boolean saveResult = calibrationMapper.save(calibrationEntity);
        return saveResult ? calibrationEntity.getId() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long log(long calibrationId, String content, boolean isCompleted) {
        CalibrationLogEntity logEntity = new CalibrationLogEntity()
            .setContent(content)
            .setCompleted(isCompleted)
            .setCalibrationId(calibrationId);
        boolean saveResult = calibrationLogMapper.save(logEntity);
        if (saveResult && isCompleted) {
            // 更新任务信息
            calibrationMapper.lambdaUpdate()
                .eq(CalibrationEntity::getId, calibrationId)
                .set(CalibrationEntity::getCompleted, logEntity.getCompleted())
                .update();
            return logEntity.getId();
        } else {
            updateThresholdTime(calibrationId);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exec(CalibrationEntity entity) {
        String execContent = "";
        boolean completed = true;
        try {
            execContent = execDataCalibrationFileList(entity.getPressureId());
        } catch (Exception ex) {
            completed = false;
            execContent = ex.getMessage();
            log.error("单次执行失败\n{}\n", entity, ex);
        } finally {
            // 记录执行信息
            log(entity.getId(), execContent, completed);
            // 组装回调信息
            PressureEntity pressureEntity = pressureService.entity(entity.getPressureId());
            if (pressureEntity == null) {
                log.warn(Message.MISS_PRESSURE, entity.getPressureId());
            } else {
                Calibration calibration = new Calibration()
                    .setContent(execContent)
                    .setCompleted(completed)
                    .setPressureId(pressureEntity.getId())
                    .setResourceId(pressureEntity.getResourceId());
                calibration.setData(entity.getId());
                // 保存回调信息
                callbackService.create(pressureEntity.getCallbackUrl(), jsonService.writeValueAsString(calibration));
            }
        }
    }

    /**
     * 执行数据校正
     *
     * @param pressureId 施压任务主键
     * @return 校正内容
     */
    private String execDataCalibrationFileList(long pressureId) throws Exception {
        // 开始记时
        long startTime = System.nanoTime();
        // 获取任务实例
        PressureEntity pressureEntity = pressureService.entity(pressureId);
        // 获取工作目录
        File directory = FileUtil.file(watchmanConfig.getNfsPath(), "metrics",
            String.valueOf(pressureEntity.getResourceId()), String.valueOf(pressureEntity.getId()));
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
                initRollBack(pressureId, rollBackFile, readyCalibrationFileList);
                // 判断回滚文件是否包含数据
                rollBackWriteLength = rollBackFile.length();
            }
        }

        // 记录状态信息
        String message = CharSequenceUtil.format("同步任务{}.耗时:{}纳秒.结果:{}.", pressureId, System.nanoTime() - startTime, rollBackWriteLength == 0);
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
     * @param pressureId   施压任务主键
     * @param rollBackFile 回滚文件
     * @param fifleList    文件列表
     */
    private void initRollBack(long pressureId, File rollBackFile, List<File> fifleList) throws Exception {
        try (OutputStream rollBackOutputStream = Files.newOutputStream(rollBackFile.toPath())) {
            try (Writer rollBackWriter = new OutputStreamWriter(rollBackOutputStream)) {
                execDataCalibrationFileList(pressureId, rollBackWriter, fifleList);
            }
        }
    }

    /**
     * 执行数据校正 - 所有文件
     *
     * @param pressureId     施压任务主键
     * @param rollBackWriter 回滚文件的writer
     * @param fileList       文件列表
     */
    private void execDataCalibrationFileList(long pressureId, Writer rollBackWriter, List<File> fileList) throws Exception {
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
                            execDataCalibrationFileLine(pressureId, rollBackWriter, line);
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
     * @param pressureId     施压任务主键
     * @param rollBackWriter 回滚文件的writer
     * @param line           文件行内容
     * @throws IOException 文件IO异常
     */
    private void execDataCalibrationFileLine(long pressureId, Writer rollBackWriter, String line) throws IOException {
        if (CharSequenceUtil.isNotBlank(line)) {
            List<MetricsInfo> metricsList = jsonService.readValue(line, new TypeReference<List<MetricsInfo>>() {
            });
            ArrayList<MetricsInfo> insertIntoInflux = new ArrayList<>(1);
            for (MetricsInfo metricsInfo : metricsList) {
                if ("response".equals(metricsInfo.getType())) {
                    insertIntoInflux.clear();
                    insertIntoInflux.add(metricsInfo);
                    execDataCalibrationItemToInflux(pressureId, insertIntoInflux, rollBackWriter);
                }
            }
        }
    }

    /**
     * 执行数据校正 - 某一行 - 写入InfluxDB
     *
     * @param pressureId     施压任务主键
     * @param data           要插入InfluxDB的数据集合
     * @param rollBackWriter 回滚文件的writer
     * @throws IOException 操作回滚的时候发生IO写入异常
     */

    void execDataCalibrationItemToInflux(long pressureId, ArrayList<MetricsInfo> data, Writer rollBackWriter) throws IOException {
        try {
            pressureMetricsService.collectorToInfluxdb(pressureId, data);
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
     * @param id 数据校准任务主键
     */
    private void updateThresholdTime(long id) {
        try {
            // 参数校验
            CalibrationEntity calibrationEntity = calibrationMapper.getById(id);
            if (calibrationEntity == null) {
                log.warn(Message.MISS_CALIBRATION, id);
                return;
            }
            // 获取错误次数
            Long logCount = calibrationLogMapper.lambdaQuery().eq(CalibrationLogEntity::getCalibrationId, calibrationEntity.getId()).count();
            // 限定参与计算的最大错误次数
            logCount = logCount > 10 ? 10 : logCount;
            // 限定阈值时间
            Date baseTime = calibrationEntity.getThresholdTime() == null ? calibrationEntity.getCreateTime() : calibrationEntity.getThresholdTime();
            // 按秒累增
            DateTime thresholdTime = DateUtil.offsetSecond(baseTime, (int)(5 * logCount));
            // 更新数据库
            calibrationMapper.lambdaUpdate()
                .set(CalibrationEntity::getThresholdTime, thresholdTime)
                .eq(CalibrationEntity::getId, calibrationEntity.getId())
                .update();
        } catch (Exception e) {
            log.error("更新阈值时间失败:{}\n", id, e);
        }
    }
}
