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
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.collector.EventMetrics;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.constants.*;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.utils.CollectorUtil;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.dao.scene.task.SceneTaskPressureTestLogUploadDAO;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private AsyncService asyncService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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

    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(100, 200,
        300L, TimeUnit.SECONDS,
        new NoLengthBlockingQueue<>(), new ThreadFactoryBuilder()
        .setNameFormat("ptl-log-push-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    public void collectorToInfluxdb(Long sceneId, Long reportId, Long customerId, List<ResponseMetrics> metricsList) {
        if (CollectionUtils.isEmpty(metricsList)) {
            return;
        }
        String measurement = InfluxUtil.getMetricsMeasurement(sceneId, reportId, customerId);
        metricsList.stream().filter(Objects::nonNull)
            .peek(metrics -> {
                //???????????????MD5???
                int strPosition = metrics.getTransaction().lastIndexOf(PressureEngineConstants.TRANSACTION_SPLIT_STR);
                if (strPosition > 0) {
                    String transaction = metrics.getTransaction();
                    metrics.setTransaction(transaction.substring(strPosition + PressureEngineConstants.TRANSACTION_SPLIT_STR.length()));
                    metrics.setTestName((transaction.substring(0, strPosition)));
                } else {
                    metrics.setTransaction(metrics.getTransaction());
                    metrics.setTestName(metrics.getTransaction());
                }
            })
            .peek(metrics -> {
                //???????????????-?????????????????????????????????influxdb??????
                if (Objects.nonNull(metrics.getTime()) && metrics.getTime() > InfluxUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTime(metrics.getTime() / 1000000);
                }
                if (metrics.getTimestamp() > InfluxUtil.MAX_ACCEPT_TIMESTAMP) {
                    metrics.setTimestamp(metrics.getTimestamp() / 1000000);
                }
            })
            .map(metrics -> InfluxUtil.toPoint(measurement, metrics.getTimestamp(), metrics))
            .forEach(influxWriter::insert);
    }

    /**
     * ????????????
     */
    public void collector(Long sceneId, Long reportId, Long tenantId, List<ResponseMetrics> metrics) {
        if (StringUtils.isNotBlank(appConfig.getCollector()) && "influxdb".equalsIgnoreCase(appConfig.getCollector())) {
            collectorToInfluxdb(sceneId, reportId, tenantId, metrics);
            return;
        }
        String taskKey = getPressureTaskKey(sceneId, reportId, tenantId);
        for (ResponseMetrics metric : metrics) {
            try {
                long timeWindow = CollectorUtil.getTimeWindowTime(metric.getTimestamp());
                if (validate(timeWindow, sceneId, reportId, tenantId, metrics)) {
                    // ??????redis
                    log.info("{}-{}-{} write redis , timestamp-{},timeWindow-{}", sceneId, reportId, tenantId,
                        metric.getTimestamp(), timeWindow);
                    String source = metric.getTransaction();
                    String transaction = source;
                    String testName = source;
                    int strPosition = metric.getTransaction().lastIndexOf(PressureEngineConstants.TRANSACTION_SPLIT_STR);
                    if (strPosition > 0) {
                        transaction = source.substring(strPosition + PressureEngineConstants.TRANSACTION_SPLIT_STR.length());
                        testName = source.substring(0, strPosition);
                    }
                    String timePod = CollectorUtil.getTimestampPodNum(metric.getTimestamp(), metric.getPodNum());
                    intSaveRedisMap(countKey(taskKey, transaction, timeWindow), timePod, metric.getCount());
                    intSaveRedisMap(failCountKey(taskKey, transaction, timeWindow), timePod, metric.getFailCount());
                    intSaveRedisMap(saCountKey(taskKey, transaction, timeWindow), timePod, metric.getSaCount());
                    intSaveRedisMap(activeThreadsKey(taskKey, transaction, timeWindow), timePod, metric.getActiveThreads());

                    // ????????????
                    setError(errorKey(taskKey, transaction, timeWindow), timePod, GsonUtil.gsonToString(metric.getErrorInfos()));
                    //1-100%??????????????????sa??????
                    saveRedisMap(percentDataKey(taskKey, transaction, timeWindow), timePod, metric.getPercentData());
                    //testName
                    saveRedisMap(testNameKey(taskKey, transaction, timeWindow), timePod, testName);
                    /*
                     * all????????????????????????????????????????????????saCount all ??????
                     */
                    intSaveRedisMap(saCountKey(taskKey, "all", timeWindow),
                        // ???????????????????????????saCount ??????????????? _transaction
                        timePod + "_" + transaction,
                        metric.getSaCount());

                    longSaveRedisMap(rtKey(taskKey, transaction, timeWindow), timePod, metric.getSumRt());
                    Double maxRt = DataUtils.getMaxRt(metric);
                    mostValue(maxRtKey(taskKey, transaction, timeWindow), maxRt, 0);
                    mostValue(minRtKey(taskKey, transaction, timeWindow), metric.getMinRt(), 1);
                }
            } catch (Exception e) {
                log.error("write redis error :{}", e.getMessage());
            }
        }
    }

    public synchronized void verifyEvent(Long sceneId, Long reportId, Long tenantId, List<EventMetrics> metrics) {
        String engineName = ScheduleConstants.getEngineName(sceneId, reportId, tenantId);
        String taskKey = getPressureTaskKey(sceneId, reportId, tenantId);

        for (EventMetrics metric : metrics) {
            try {
                // ?????????pod
                boolean isFirst = METRICS_EVENTS_STARTED.equals(metric.getEventName());
                boolean isLast = METRICS_EVENTS_ENDED.equals(metric.getEventName());
                //??????pod?????????????????????????????????????????????????????????
                String enginePodNoStartKey = ScheduleConstants.getEnginePodNoStartKey(sceneId, reportId, tenantId,
                    metric.getPodNo(), metric.getEventName());
                Long startPod = stringRedisTemplate.opsForValue().increment(enginePodNoStartKey, 1);
                if (startPod != null && startPod > 1) {
                    continue;
                }
                if (isFirst) {
                    // ???????????????????????????????????????
                    if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(forceCloseTime(taskKey)))) {
                        // ??????????????????
                        log.info("????????????{}-{}-{}:??????????????????????????????-{}", sceneId, reportId, tenantId, metric.getTimestamp());
                        SceneManageWrapperOutput wrapperDTO = sceneManageService.getSceneManage(sceneId, new SceneManageQueryOpitons());
                        setForceCloseTime(forceCloseTime(taskKey), metric.getTimestamp(), wrapperDTO.getPressureTestSecond());
                    }
                    // ???min
                    setMin(engineName + ScheduleConstants.FIRST_SIGN, metric.getTimestamp());
                    //?????????????????? ???????????? ?????????????????? ????????????????????????????????????????????????????????????redis?????? ????????????????????????
                    // ???????????? running -- > ?????????????????????
                    // ?????? ??????????????????????????????
                    Long count = stringRedisTemplate.opsForValue().increment(engineName, 1);

                    if (count != null && count == 1) {
                        sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
                            .checkEnum(SceneManageStatusEnum.PRESSURE_NODE_RUNNING)
                            .updateEnum(SceneManageStatusEnum.ENGINE_RUNNING)
                            .build());
                        notifyStart(sceneId, reportId, metric.getTimestamp());
                        cacheTryRunTaskStatus(sceneId, reportId, tenantId, SceneRunTaskStatusEnum.RUNNING);
                    }
                    //?????????cloud???????????????????????????????????????????????????????????????ptl????????????
                    if (PressureLogUploadConstants.UPLOAD_BY_CLOUD.equals(appConfig.getEngineLogUploadModel())) {
                        log.info("??????????????????ptl???????????????ID???{},??????ID:{},PodNum:{}", sceneId, reportId, metric.getPodNo());
                        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
                        String fileName = metric.getTags().get(SceneTaskRedisConstants.CURRENT_PTL_FILE_NAME_SYSTEM_PROP_KEY);
                        THREAD_POOL.submit(new PressureTestLogUploadTask(sceneId, reportId, tenantId, logUploadDAO, stringRedisTemplate,
                            pushLogService, sceneManageDAO, ptlDir, fileName, engineCallExtApi) {});
                    }
                }
                if (isLast) {
                    // ???max flag ???????????????
                    Long engineNameNum = Optional.ofNullable(redisTemplate.opsForValue().get(engineName)).map(String::valueOf).map(Long::valueOf).orElse(0L);
                    if (engineNameNum.equals(1L)) {
                        // ?????????????????????????????? ????????????
                        log.info("????????????{}-{}-{}:??????????????????", sceneId, reportId, tenantId);
                        setLast(last(taskKey), ScheduleConstants.LAST_SIGN);
                        setMax(engineName + ScheduleConstants.LAST_SIGN, metric.getTimestamp());
                        notifyEnd(sceneId, reportId, metric.getTimestamp(), tenantId);
                        return;
                    }
                    // ?????? ??????????????????
                    Long tempLastSignCount = stringRedisTemplate.opsForValue().increment(ScheduleConstants.TEMP_LAST_SIGN + engineName, 1);
                    // ????????????????????????????????? ???????????? == ????????????????????????
                    if (isLastSign(tempLastSignCount, engineName)) {
                        // ??????????????????
                        log.info("????????????{}-{}-{}:??????????????????", sceneId, reportId, tenantId);
                        setLast(last(taskKey), ScheduleConstants.LAST_SIGN);
                        setMax(engineName + ScheduleConstants.LAST_SIGN, metric.getTimestamp());
                        // ??????????????????
                        stringRedisTemplate.delete(ScheduleConstants.TEMP_LAST_SIGN + engineName);
                        // ????????????
                        notifyEnd(sceneId, reportId, metric.getTimestamp(), tenantId);
                    }
                }
            } catch (Exception e) {
                log.error("???????????????{}???,????????????????????????????????????????????????????????? --> ???Collector-metrics-Error??????????????????????????????????????????: {}",
                    TakinCloudExceptionEnum.TASK_RUNNING_RECEIVE_PT_DATA_ERROR, e);
            }
        }

    }

    private void cacheTryRunTaskStatus(Long sceneId, Long reportId, Long customerId, SceneRunTaskStatusEnum status) {
        taskStatusCache.cacheStatus(sceneId, reportId, status);
        Report report = tReportMapper.selectByPrimaryKey(reportId);
        if (Objects.nonNull(report) && !report.getPressureType().equals(PressureSceneEnum.FLOW_DEBUG.getCode())
            && !report.getPressureType().equals(PressureSceneEnum.INSPECTION_MODE.getCode())
            && status.getCode() == SceneRunTaskStatusEnum.RUNNING.getCode()) {
            asyncService.updateSceneRunningStatus(sceneId, reportId, customerId);
        }
    }

    private void notifyStart(Long sceneId, Long reportId, long startTime) {
        log.info("??????[{}]?????????????????????????????????[{}]????????????[{}]", sceneId, reportId, startTime);
        reportDao.updateReportStartTime(reportId, new Date(startTime));
    }

    private void notifyEnd(Long sceneId, Long reportId, long endTime, Long tenantId) {
        log.info("??????[{}]?????????????????????,??????????????????{}", sceneId, reportId);
        // ?????????????????????Redis??????
        taskStatusCache.cacheStatus(sceneId, reportId, SceneRunTaskStatusEnum.ENDED);
        // ????????????????????????  ?????????????????????,???????????????????????? ---->????????????????????????
        sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
            .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
            .updateEnum(SceneManageStatusEnum.STOP)
            .build());
        reportDao.updateReportEndTime(reportId, new Date(endTime));
    }

    private boolean isLastSign(Long lastSignCount, String engineName) {
        String redisResult = stringRedisTemplate.opsForValue().get(engineName);
        // redis???????????? ???????????????
        return StringUtils.isNotEmpty(redisResult) && lastSignCount.equals(Long.valueOf(redisResult));
    }

    /**
     * ????????????????????????pod????????????
     */
    public void statisticalIp(Long sceneId, Long reportId, Long tenantId, long time, String ip) {

        String windowsTimeKey = String.format("%s:%s", getPressureTaskKey(sceneId, reportId, tenantId),
            "windowsTime");
        String timeInMillis = String.valueOf(CollectorUtil.getTimeWindowTime(time));
        List<String> ips;
        Long windowsTimeValue = redisTemplate.getExpire(windowsTimeKey);
        if (Long.valueOf(-2L).equals(windowsTimeValue)) {
            ips = new ArrayList<>();
            ips.add(ip);
            redisTemplate.opsForHash().put(windowsTimeKey, timeInMillis, ips);
            redisTemplate.expire(windowsTimeKey, 60 * 60 * 2, TimeUnit.SECONDS);
        } else {
            Object cacheData = redisTemplate.opsForHash().get(windowsTimeKey, timeInMillis);
            if (cacheData instanceof List) {
                ips = ((List<?>)cacheData).stream()
                    .filter(t -> t instanceof String)
                    .map(Object::toString)
                    .collect(Collectors.toList());
            } else {
                ips = new ArrayList<>(0);
            }
            redisTemplate.opsForHash().put(windowsTimeKey, timeInMillis, ips);
        }

    }

    /**
     * ????????????????????????
     *
     * @return -
     */
    private boolean validate(long time, Long sceneId, Long reportId, Long tenantId, List<ResponseMetrics> metrics) {
        if ((System.currentTimeMillis() - time) > CollectorConstants.OVERDUE_TIME) {
            log.info("{}-{}-{}????????????,????????????{}??????????????????{}", sceneId, reportId, tenantId,
                System.currentTimeMillis() - time, JsonHelper.bean2Json(metrics));
            return false;
        }
        return true;
    }

}
