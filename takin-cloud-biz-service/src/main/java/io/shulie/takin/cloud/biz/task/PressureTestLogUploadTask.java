package io.shulie.takin.cloud.biz.task;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.biz.utils.FileFetcher;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.biz.service.log.PushLogService;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.data.model.mysql.ScenePressureTestLogUploadEntity;
import io.shulie.takin.cloud.data.dao.scene.task.SceneTaskPressureTestLogUploadDAO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

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
    private String logDir;
    private String fileName;
    private PushLogService pushLogService;
    private SceneManageDAO sceneManageDAO;
    private EngineCallExtApi engineCallExtApi;
    private StringRedisTemplate stringRedisTemplate;
    private SceneTaskPressureTestLogUploadDAO logUploadDAO;

    /**
     * 构造函数
     *
     * @param sceneId             场景主键
     * @param reportId            报告主键
     * @param tenantId            租户主键
     * @param logUploadDAO        日志上传DAO
     * @param stringRedisTemplate redis操作类
     * @param pushLogService      日志上传服务
     * @param sceneManageDAO      场景管理DAO
     * @param logDir              日志目录
     * @param fileName            文件名称
     * @param engineCallExtApi    引擎插件
     */
    public PressureTestLogUploadTask(Long sceneId, Long reportId, Long tenantId,
        SceneTaskPressureTestLogUploadDAO logUploadDAO, StringRedisTemplate stringRedisTemplate,
        PushLogService pushLogService, SceneManageDAO sceneManageDAO,
        String logDir, String fileName, EngineCallExtApi engineCallExtApi) {
        this.sceneId = sceneId;
        this.reportId = reportId;
        this.tenantId = tenantId;
        this.logUploadDAO = logUploadDAO;
        this.stringRedisTemplate = stringRedisTemplate;
        this.pushLogService = pushLogService;
        this.sceneManageDAO = sceneManageDAO;
        this.logDir = logDir;
        this.fileName = fileName;
        this.engineCallExtApi = engineCallExtApi;
    }

    private static final String VERSION = "1.6";

    private static final Long MAX_PUSH_SIZE = 1024L * 1024L;

    private static final int MAX_WAIT_TIME = 1500;

    /**
     * 上传PTL文件
     */
    private void uploadPtlFile() {
        String filePath = String.format("%s/ptl/%d/%d/%s",logDir, this.sceneId, this.reportId, fileName);
        log.info("上传压测明细日志--文件路径：{}", filePath);
        //解决报告已完成，但是文件还未生成，上传大小未0
        int waitCount = 0;
        while (null == getFile(filePath) && waitCount < MAX_WAIT_TIME) {
            try {
                log.info("上传Jmeter日志--场景ID:{},文件未生成{},休眠等待", this.sceneId, filePath);
                TimeUnit.SECONDS.sleep(2);
                waitCount++;
            } catch (InterruptedException e) {
                cleanCache(this.fileName.replaceAll("\\.", ""));
                log.warn("上传Jmeter日志--场景ID:{},休眠失败，文件路径【{}】", this.sceneId, filePath);
                Thread.currentThread().interrupt();
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
                if (data.length == 0) {
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
                        long fileSize = 0;
                        for (int i = 0; i < 10; i++) {
                            fileSize = getFileSize(ptlFile);
                            if (fileSize == 0) {log.info("文件大小是0，等一会再重试.第{}次.", i + 1);}
                            Thread.sleep(1000);
                        }
                        long lastSize = Math.max(fileSize - position, MAX_PUSH_SIZE);
                        data = readFile(ptlFile, subFileName, position, ptlFile.getAbsolutePath(), fileFetcher, lastSize);
                        if (data.length > 0) {
                            pushLogService.pushLogToAmdb(data, VERSION);
                        } else if (lastSize > 0) {
                            TimeUnit.SECONDS.sleep(10);
                            data = readFile(ptlFile, subFileName, position, ptlFile.getAbsolutePath(), fileFetcher, lastSize);
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
            } catch (InterruptedException e) {
                cleanCache(subFileName);
                log.error("异常代码【{}】,异常内容：推送日志到amdb异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, e);
                Thread.currentThread().interrupt();
                return;
            } catch (Throwable e) {
                cleanCache(subFileName);
                log.error("异常代码【{}】,异常内容：推送日志到amdb异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, e);
                return;
            }
        }
    }

    /**
     * 清除缓存
     *
     * @param fileName 文件名称
     */
    private void cleanCache(String fileName) {
        String statusKey = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", this.sceneId,
            this.reportId);
        stringRedisTemplate.opsForHash().delete(SceneTaskRedisConstants.PRESSURE_TEST_LOG_UPLOAD_RECORD,
            String.format("%d_%d_%s", this.sceneId, this.reportId, fileName));
        stringRedisTemplate.opsForHash().put(statusKey, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY,
            SceneRunTaskStatusEnum.ENDED.getText());
    }

    /**
     * 缓存记录上传的行数
     *
     * @param fileName 文件名称
     * @param position 点位
     */
    private void cacheFileUploadedPosition(String fileName, Long position) {
        stringRedisTemplate.opsForHash().put(SceneTaskRedisConstants.PRESSURE_TEST_LOG_UPLOAD_RECORD,
            String.format("%d_%d_%s", this.sceneId, this.reportId, fileName),
            String.valueOf(position));
    }

    /**
     * 获取文件位点
     *
     * @param fileName 文件名称
     * @return -
     */
    private Long getPosition(String fileName) {
        Object position = stringRedisTemplate.opsForHash()
            .get(SceneTaskRedisConstants.PRESSURE_TEST_LOG_UPLOAD_RECORD,
                String.format("%d_%d_%s", this.sceneId, this.reportId, fileName));
        if (Objects.isNull(position)) {
            return 0L;
        } else {
            return Long.parseLong(position.toString());
        }
    }

    /**
     * 获取文件大小
     *
     * @param file 文件路径
     * @return 除非文件存在且是一个文件(不是文件夹), 否则返回0
     */

    private long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }

    /**
     * 场景是否结束
     *
     * @param sceneId 场景主键
     * @return true/false
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
     * @param position 点位信息
     * @param filePath 文件路径
     * @return -
     * @throws IOException IO异常
     */
    private byte[] readFile(File file, String subFileName, Long position, String filePath, FileFetcher fileFetcher,
        long pushSize)
        throws IOException {
        if (!file.exists() || !file.isFile()) {
            log.warn("上传压测明细日志--读取文件【{}】失败：文件不存在或非文件", filePath);
            return new byte[0];
        }
        byte[] data = fileFetcher.read(position, pushSize);
        //已经读到当前行，等待文件继续写入
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        log.debug("上传压测明细日志--读取到文件大小:【{}】", data.length);
        position += data.length;
        cacheFileUploadedPosition(subFileName, position);
        return data;
    }

    /**
     * 创建上传记录
     *
     * @param fileName 文件名称
     * @param fileSize 文件大小
     */
    private void createUploadRecord(Long sceneId, Long reportId, Long tenantId, String fileName, Long fileSize) {
        log.info("上传压测明细日志--文件【{}】上传完成，创建上传记录", fileName);
        ScenePressureTestLogUploadEntity entity = new ScenePressureTestLogUploadEntity();
            entity.setSceneId(sceneId);
            entity.setReportId(reportId);
            entity.setTenantId(tenantId);
            entity.setFileName(fileName);
            entity.setTaskStatus(SceneRunTaskStatusEnum.ENDED.getCode());
            entity.setUploadStatus(2);
            entity.setCreateTime(new Date());
            entity.setUploadCount(fileSize);
        int insertRows = this.logUploadDAO.insertRecord(entity);
        if (insertRows == 1) {
            log.info("上传压测明细日志--创建上传记录成功:sceneID:【{}】,reportID:【{}】,fileName:【{}】", sceneId, reportId, fileName);
        } else {
            log.error("异常代码【{}】,异常内容：上传压测明细日志--创建上传记录失败:sceneID:【{}】,reportID:【{}】,fileName:【{}",
                TakinCloudExceptionEnum.TASK_RUNNING_LOG_PUSH_ERROR, sceneId, reportId, fileName);
        }
    }

    /**
     * 获取文件
     *
     * @param filePath 文件路径
     * @return 除非文件存在且是一个文件(不是文件夹), 否则返回null
     */
    private File getFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
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
