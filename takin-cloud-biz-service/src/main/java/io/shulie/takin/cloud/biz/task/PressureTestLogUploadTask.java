package io.shulie.takin.cloud.biz.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.biz.service.log.PushLogService;
import io.shulie.takin.cloud.biz.utils.FileFetcher;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.dao.scene.task.SceneTaskPressureTestLogUploadDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.model.mysql.ScenePressureTestLogUploadEntity;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 压测日志上传任务
 *
 * @author -
 */
@Data
@Slf4j
public class PressureTestLogUploadTask implements Runnable {

    private Long sceneId;

    private Long reportId;

    private Long tenantId;

    private SceneTaskPressureTestLogUploadDAO logUploadDAO;

    private RedisClientUtils redisClientUtils;

    private PushLogService pushLogService;

    private SceneManageDAO sceneManageDAO;

    private String logDir;

    private String fileName;

    private EngineCallExtApi engineCallExtApi;

    public PressureTestLogUploadTask(Long sceneId, Long reportId, Long tenantId,
        SceneTaskPressureTestLogUploadDAO logUploadDAO, RedisClientUtils redisClientUtils,
        PushLogService pushLogService, SceneManageDAO sceneManageDAO,
        String logDir, String fileName, EngineCallExtApi engineCallExtApi) {
        this.sceneId = sceneId;
        this.reportId = reportId;
        this.tenantId = tenantId;
        this.logUploadDAO = logUploadDAO;
        this.redisClientUtils = redisClientUtils;
        this.pushLogService = pushLogService;
        this.sceneManageDAO = sceneManageDAO;
        this.logDir = logDir;
        this.fileName = fileName;
        this.engineCallExtApi = engineCallExtApi;
    }

    private static final String VERSION = "1.6";

    private static final Long MAX_PUSH_SIZE = 1024L * 1024L;

    private void uploadPtlFile() {
        String filePath = String.format(logDir + "/ptl/%s/%s/%s", this.sceneId, this.reportId, fileName);
        log.info("上传压测明细日志--文件路径：{}", filePath);
        //解决报告已完成，但是文件还未生成，上传大小未0
        while (null == getFile(filePath)) {
            try {
                log.info("上传Jmeter日志--场景ID:{},文件未生成{},休眠等待", this.sceneId, filePath);
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                cleanCache(this.fileName.replaceAll("\\.", ""));
                log.warn("上传Jmeter日志--场景ID:{},休眠失败，文件路径【{}】", this.sceneId, filePath);
                return;
            }
        }
        File ptlFile = getFile(filePath);
        if (ptlFile == null) {
            log.error("异常代码【{}】,异常内容：上传压测明细日志失败 --> 上传压测明细日志--【{}】不存在或者不是文件",
                TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, filePath);
            return;
        }
        //去掉特殊字符
        String subFileName = ptlFile.getName().replaceAll("\\.", "");
        byte[] data;
        FileFetcher fileFetcher;
        try {
            fileFetcher = new FileFetcher(ptlFile);
        } catch (FileNotFoundException e) {
            log.error("异常代码【{}】,异常内容：上传压测明细日志失败 --> 获取fileFetcher出错,文件：【{}】,异常信息【{}】",
                TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, filePath, e);
            cleanCache(subFileName);
            return;
        }
        while (true) {
            try {
                Long position = getPosition(subFileName);
                data = readFile(ptlFile, subFileName, position, ptlFile.getAbsolutePath(), fileFetcher, MAX_PUSH_SIZE);
                // 如果没有读到数据需要判断是不是报告已经完成，如果报告已经完成说明任务已经结束，并且日志都已经推送完成，这时就可以结束这个文件的推送任务
                //否则下一次继续读取
                if (data == null || data.length == 0) {
                    //场景状态是否是完成
                    boolean sceneEnded = isSceneEnded(this.sceneId);
                    if (sceneEnded) {
                        //等待job删除
                        String jobName = ScheduleConstants.getScheduleName(sceneId, reportId, tenantId);
                        while (SceneManageConstant.SCENE_TASK_JOB_STATUS_RUNNING.equals(engineCallExtApi.getJobStatus(jobName))) {
                            log.info("上传Jmeter日志--场景ID:{},job【{}】还在运行中，等待job停止", sceneId, jobName);
                            TimeUnit.SECONDS.sleep(5);
                        }
                        log.info("上传Jmeter日志--场景ID:{}，报告ID:{},job【{}】已停止，最后一次上传", this.sceneId, this.reportId, jobName);
                        long fileSize = getFileSize(ptlFile);
                        long lastSize = Math.max(fileSize - position, MAX_PUSH_SIZE);
                        data = readFile(ptlFile, subFileName, position, ptlFile.getAbsolutePath(), fileFetcher,
                            lastSize);
                        if (data != null && data.length > 0) {
                            pushLogService.pushLogToAmdb(data, VERSION);
                        }else if(lastSize > 0){
                            TimeUnit.SECONDS.sleep(10);
                            data = readFile(ptlFile, subFileName, position, ptlFile.getAbsolutePath(), fileFetcher,
                                lastSize);
                            pushLogService.pushLogToAmdb(data, VERSION);
                        }
                        position = getPosition(subFileName);
                        log.info("上传Jmeter日志--场景ID:{},文件【{}】上传完成，文件大小【{}】", this.sceneId, this.fileName, position);
                        //删除缓存的key，记录文件上传大小
                        createUploadRecord(this.sceneId, this.reportId, this.tenantId,
                            ptlFile.getAbsolutePath(), position);
                        cleanCache(subFileName);
                        fileFetcher.close();
                        break;
                    }
                    Thread.sleep(1000);
                    continue;
                }
                pushLogService.pushLogToAmdb(data, VERSION);
            } catch (Throwable e) {
                cleanCache(subFileName);
                log.error("异常代码【{}】,异常内容：推送日志到amdb异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, e);
                return;
            }
        }
    }

    private void cleanCache(String fileName) {
        String statusKey = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", this.sceneId,
            this.reportId);
        redisClientUtils.hmdelete(SceneTaskRedisConstants.PRESSURE_TEST_LOG_UPLOAD_RECORD,
            String.format("%s_%s_%s", this.sceneId,
                this.reportId, fileName));
        redisClientUtils.hmset(statusKey, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY,
            SceneRunTaskStatusEnum.ENDED.getText());
    }

    /**
     * 记录上传的行数
     *
     * @param fileName -
     * @param position -
     */
    private void cacheFileUploadedPosition(String fileName, Long position) {
        this.redisClientUtils.hmset(SceneTaskRedisConstants.PRESSURE_TEST_LOG_UPLOAD_RECORD,
            String.format("%s_%s_%s", this.sceneId, this.reportId, fileName),
            position);
    }

    /**
     * 获取上传的位置
     *
     * @param fileName -
     * @return -
     */
    private Long getPosition(String fileName) {
        Object position = this.redisClientUtils.hmget(SceneTaskRedisConstants.PRESSURE_TEST_LOG_UPLOAD_RECORD,
            String.format("%s_%s_%s", this.sceneId, this.reportId, fileName));
        if (Objects.isNull(position)) {
            return 0L;
        } else {
            return Long.parseLong(position.toString());
        }
    }

    private long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }

    /**
     * @param sceneId 场景主键
     * @return 是否启用
     */
    private boolean isSceneEnded(Long sceneId) {
        SceneManageEntity manageResult = this.sceneManageDAO.getSceneById(sceneId);
        if (Objects.isNull(manageResult) || Objects.isNull(manageResult.getStatus())) {
            log.warn("上传Jmeter日志--场景ID:{},未查询到场景！", sceneId);
            return true;
        }
        return SceneManageStatusEnum.ifFinished(manageResult.getStatus());
    }

    /**
     * 从指定定行数开始，读取文件的剩余内容
     *
     * @param position -
     * @param filePath -
     * @return -
     * @throws IOException -
     */
    private byte[] readFile(File file, String subFileName, Long position, String filePath, FileFetcher fileFetcher,
        long pushSize)
        throws IOException {
        if (!file.exists() || !file.isFile()) {
            log.warn("上传压测明细日志--读取文件【{}】失败：文件不存在或非文件", filePath);
            return null;
        }
        byte[] data = fileFetcher.read(position, pushSize);
        //已经读到当前行，等待文件继续写入
        if (data == null || data.length == 0) {
            return data;
        }
        log.debug("上传压测明细日志--读取到文件大小:【{}】", data.length);
        position += data.length;
        cacheFileUploadedPosition(subFileName, position);
        return data;
    }

    /**
     * 创建上传记录
     *
     * @param fileName -
     * @param fileSize -
     */
    private void createUploadRecord(Long sceneId, Long reportId, Long tenantId, String fileName, Long fileSize) {
        log.info("上传压测明细日志--文件【{}】上传完成，创建上传记录", fileName);
        ScenePressureTestLogUploadEntity entity = new ScenePressureTestLogUploadEntity() {{
            setSceneId(sceneId);
            setReportId(reportId);
            setTenantId(tenantId);
            setFileName(fileName);
            setTaskStatus(SceneRunTaskStatusEnum.ENDED.getCode());
            setUploadStatus(2);
            setCreateTime(new Date());
            setUploadCount(fileSize);
        }};
        int record = this.logUploadDAO.insertRecord(entity);
        if (record == 1) {
            log.info("上传压测明细日志--创建上传记录成功:sceneID:【{}】,reportID:【{}】,fileName:【{}】", sceneId, reportId, fileName);
        } else {
            log.error("异常代码【{}】,异常内容：上传压测明细日志--创建上传记录失败:sceneID:【{}】,reportID:【{}】,fileName:【{}",
                TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, sceneId, reportId, fileName);
        }
    }

    private File getFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file;
        }
        return null;
    }

    @Override
    public void run() {
        long beginTime = System.currentTimeMillis();
        log.info("上传压测明细日志--任务启动：线程id-{},sceneId-{},reportId-{},开始时间：{}", Thread.currentThread().getId(),
            this.sceneId, this.reportId, beginTime);
        uploadPtlFile();
        long endTime = System.currentTimeMillis();
        log.info("上传压测明细日志--任务完成:线程id-{},sceneId-{},reportId-{},结束时间：{},耗时：{}", Thread.currentThread().getId(),
            this.sceneId, this.reportId, endTime,
            endTime - beginTime);
    }
}
