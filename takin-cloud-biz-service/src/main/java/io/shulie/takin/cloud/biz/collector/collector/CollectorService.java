package io.shulie.takin.cloud.biz.collector.collector;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pamirs.takin.entity.dao.report.TReportMapper;
import com.pamirs.takin.entity.domain.entity.report.Report;
import io.shulie.takin.cloud.biz.cache.SceneTaskStatusCache;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.async.AsyncService;
import io.shulie.takin.cloud.biz.service.log.PushLogService;
import io.shulie.takin.cloud.biz.task.PressureTestLogUploadTask;
import io.shulie.takin.cloud.common.bean.collector.EventMetrics;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.constants.*;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.influxdb.InfluxDBUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.common.utils.CollectorUtil;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.sceneTask.SceneTaskPressureTestLogUploadDAO;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import io.shulie.takin.ext.api.EngineCallExtApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 14:38
 */
@Slf4j
@Service
public class CollectorService extends AbstractIndicators {

    public static final String METRICS_EVENTS_STARTED = "started";
    public static final String METRICS_EVENTS_ENDED = "ended";
    @Resource
    private TReportMapper tReportMapper;
    @Resource
    private RedisClientUtils redisClientUtils;
    @Resource
    private AsyncService asyncService;
    @Resource
    private SceneTaskPressureTestLogUploadDAO logUploadDAO;
    @Resource
    private PushLogService pushLogService;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private ReportDao reportDao;
    @Resource
    private SceneTaskStatusCache taskStatusCache;

    @Value("${script.path}")
    private String ptlDir;
    @Resource
    private EnginePluginUtils enginePluginUtils;
    @Resource
    private InfluxWriter influxWriter;
    @Resource
    private AppConfig appConfig;

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(100, 200,
        300L, TimeUnit.SECONDS,
        new NoLengthBlockingQueue<>(), new ThreadFactoryBuilder()
        .setNameFormat("ptl-log-push-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    public void collectorToInfluxdb(Long sceneId, Long reportId, Long customerId, List<ResponseMetrics> metricses) {
        if (CollectionUtils.isEmpty(metricses)) {
            return;
        }
        String measurement = InfluxDBUtil.getMetricsMeasurement(sceneId, reportId, customerId);
        metricses.stream().filter(Objects::nonNull)
            .map(metrics -> {
                //判断有没有MD5值
                int strPosition = metrics.getTransaction().lastIndexOf(PressureEngineConstants.TRANSACTION_SPLIT_STR);
                if (strPosition > 0) {
                    String transaction = metrics.getTransaction();
                    metrics.setTransaction(transaction.substring(strPosition + PressureEngineConstants.TRANSACTION_SPLIT_STR.length()));
                    metrics.setTestName((transaction.substring(0, strPosition)));
                } else {
                    metrics.setTransaction(metrics.getTransaction());
                    metrics.setTestName(metrics.getTransaction());
                }
                //处理时间戳-纳秒转成毫秒，防止插入influxdb报错
                if (Objects.nonNull(metrics.getTime()) && metrics.getTime() > InfluxDBUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTime(metrics.getTime() / 1000000);
                }
                if (metrics.getTimestamp() > InfluxDBUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTimestamp(metrics.getTimestamp() / 1000000);
                }
                return InfluxDBUtil.toPoint(measurement, metrics.getTimestamp(), metrics);
            })
            .forEach(influxWriter::insert);
    }

    /**
     * 记录时间
     */
    public void collector(Long sceneId, Long reportId, Long customerId, List<ResponseMetrics> metrics) {
        collectorToInfluxdb(sceneId, reportId, customerId, metrics);
    }

    public synchronized void verifyEvent(Long sceneId, Long reportId, Long customerId, List<EventMetrics> metrics) {
        String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
        String taskKey = getPressureTaskKey(sceneId, reportId, customerId);

        for (EventMetrics metric : metrics) {
            try {
                // 解决多pod
                boolean isFirst = METRICS_EVENTS_STARTED.equals(metric.getEventName());
                boolean isLast = METRICS_EVENTS_ENDED.equals(metric.getEventName());
                //每个pod只会启动或者一次，处理数据重复发送问题
                String enginePodNoStartKey = ScheduleConstants.getEnginePodNoStartKey(sceneId, reportId, customerId,
                    metric.getPodNo(), metric.getEventName());
                Long startPod = redisClientUtils.increment(enginePodNoStartKey, 1);
                if (startPod > 1) {
                    continue;
                }
                if (isFirst) {
                    // 超时自动检修，强行触发关闭
                    if (!redisClientUtils.hasKey(forceCloseTime(taskKey))) {
                        // 获取压测时长
                        log.info("本次压测{}-{}-{}:记录超时自动检修时间-{}", sceneId, reportId, customerId, metric.getTimestamp());
                        SceneManageWrapperOutput wrapperDTO = sceneManageService.getSceneManage(sceneId, new SceneManageQueryOpitons());
                        setForceCloseTime(forceCloseTime(taskKey), metric.getTimestamp(), wrapperDTO.getPressureTestSecond());
                    }
                    // 取min
                    setMin(engineName + ScheduleConstants.FIRST_SIGN, metric.getTimestamp());
                    //多个压力节点 解决方案 只要一个节点 过来，状态就是压测引擎已启动，但是会通过redis计数 数据将归属于报告
                    // 压力节点 running -- > 压测引擎已启动
                    // 计数 压测引擎实际运行个数
                    Long count = redisClientUtils.increment(engineName, 1);

                    if (count != null && count == 1) {
                        sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, customerId)
                            .checkEnum(SceneManageStatusEnum.PRESSURE_NODE_RUNNING)
                            .updateEnum(SceneManageStatusEnum.ENGINE_RUNNING)
                            .build());
                        cacheTryRunTaskStatus(sceneId, reportId, customerId, SceneRunTaskStatusEnum.RUNNING);
                    }
                    //如果从cloud上传请求流量明细，则需要启动异步线程去读取ptl文件上传
                    if (PressureLogUploadConstants.UPLOAD_BY_CLOUD.equals(appConfig.getEngineLogUploadModel())) {
                        log.info("开始异步上传ptl日志，场景ID：{},报告ID:{},PodNum:{}", sceneId, reportId, metric.getPodNo());
                        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
                        String fileName = metric.getTags().get(SceneTaskRedisConstants.CURRENT_JTL_FILE_NAME_SYSTEM_PROP_KEY);
                        THREAD_POOL.submit(new PressureTestLogUploadTask(sceneId, reportId, customerId, logUploadDAO, redisClientUtils,
                            pushLogService, sceneManageDAO, ptlDir, fileName, engineCallExtApi) {
                        });
                    }
                }
                if (isLast) {
                    // 取max flag 是否更新过
                    Long engineNameNum = Optional.ofNullable(redisTemplate.opsForValue().get(engineName)).map(String::valueOf).map(Long::valueOf).orElse(0L);
                    if (engineNameNum.equals(1L)) {
                        // 压测引擎只有一个运行 压测停止
                        log.info("本次压测{}-{}-{}:打入结束标识", sceneId, reportId, customerId);
                        setLast(last(taskKey), ScheduleConstants.LAST_SIGN);
                        setMax(engineName + ScheduleConstants.LAST_SIGN, metric.getTimestamp());
                        notifyEnd(sceneId, reportId, metric.getTimestamp());
                        return;
                    }
                    // 计数 回传标识数量
                    Long tempLastSignCount = redisClientUtils.increment(ScheduleConstants.TEMP_LAST_SIGN + engineName, 1);
                    // 是否是最后一个结束标识 回传个数 == 压测实际运行个数
                    if (isLastSign(tempLastSignCount, engineName)) {
                        // 标识结束标识
                        log.info("本次压测{}-{}-{}:打入结束标识", sceneId, reportId, customerId);
                        setLast(last(taskKey), ScheduleConstants.LAST_SIGN);
                        setMax(engineName + ScheduleConstants.LAST_SIGN, metric.getTimestamp());
                        // 删除临时标识
                        redisClientUtils.del(ScheduleConstants.TEMP_LAST_SIGN + engineName);
                        // 压测停止
                        notifyEnd(sceneId, reportId, metric.getTimestamp());
                    }
                }
            } catch (Exception e) {
                log.error("异常代码【{}】,异常内容：接收压测引擎回传事件数据异常 --> 【Collector-metrics-Error】接收处理事件数据，异常信息: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_RECEIVE_PT_DATA_ERROR, e);
            }
        }
    }

    private void cacheTryRunTaskStatus(Long sceneId, Long reportId, Long customerId, SceneRunTaskStatusEnum status) {
        taskStatusCache.cacheStatus(sceneId, reportId, status);
        Report report = tReportMapper.selectByPrimaryKey(reportId);
        if (Objects.nonNull(report) && report.getPressureType() != PressureSceneEnum.FLOW_DEBUG.getCode()
            && report.getPressureType() != PressureSceneEnum.INSPECTION_MODE.getCode()
            && status.getCode() == SceneRunTaskStatusEnum.RUNNING.getCode()) {
            asyncService.updateSceneRunningStatus(sceneId, reportId, customerId);
        }
    }

    private void notifyEnd(Long sceneId, Long reportId, long endTime) {
        log.info("场景[{}]压测任务已完成,更新结束时间{}", sceneId, reportId);
        reportDao.updateReportEndTime(reportId, new Date(endTime));
    }

    private boolean isLastSign(Long lastSignCount, String engineName) {
        return StringUtils.isNotEmpty(redisClientUtils.getString(engineName))
            && lastSignCount.equals(Long.valueOf(redisClientUtils.getString(engineName)));
    }

    /**
     * 统计每个时间窗口pod调用数量
     */
    public void statisticalIp(Long sceneId, Long reportId, Long customerId, long time, String ip) {
        String windosTimeKey = String.format("%s:%s", getPressureTaskKey(sceneId, reportId, customerId),
            "windosTime");
        String timeInMillis = String.valueOf(CollectorUtil.getTimeWindow(time).getTimeInMillis());
        List<String> ips = null;
        if (Objects.equals(redisTemplate.getExpire(windosTimeKey), -2L)) {
            ips = new ArrayList<>();
            ips.add(ip);
            redisTemplate.opsForHash().put(windosTimeKey, timeInMillis, ips);
            redisTemplate.expire(windosTimeKey, 60 * 60 * 2L, TimeUnit.SECONDS);
        } else {
            Object o = redisTemplate.opsForHash().get(windosTimeKey, timeInMillis);
            if (o instanceof List) {
                ips = ((List<?>)o).stream().map(Object::toString).collect(Collectors.toList());
            }
            if (null == ips) {ips = new ArrayList<>();}
            ips.add(ip);
            redisTemplate.opsForHash().put(windosTimeKey, timeInMillis, ips);
        }
    }
}
