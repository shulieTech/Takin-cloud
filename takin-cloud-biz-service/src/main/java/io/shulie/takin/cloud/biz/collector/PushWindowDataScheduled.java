package io.shulie.takin.cloud.biz.collector;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;

import org.apache.ibatis.jdbc.SQL;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.cloud.common.utils.*;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.biz.utils.Executors;
import io.shulie.takin.eventcenter.entity.TaskConfig;
import io.shulie.takin.ext.content.script.ScriptNode;
import io.shulie.takin.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.influxdb.InfluxDBUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import io.shulie.takin.cloud.common.constants.CollectorConstants;
import io.shulie.takin.cloud.biz.output.statistics.PressureOutput;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageResult;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 22:13
 */
@Slf4j
@Component
public class PushWindowDataScheduled extends AbstractIndicators {

    @Resource
    private ReportDao reportDao;
    @Resource
    private InfluxWriter influxWriter;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private EventCenterTemplate eventCenterTemplate;

    @Value("${scheduling.enabled:true}")
    private Boolean schedulingEnabled;

    @Value("${report.metric.isSaveLastPoint:true}")
    private boolean isSaveLastPoint;

    public void sendMetrics(Metrics metrics) {
        Event event = new Event();
        event.setEventName("metricsData");
        event.setExt(metrics);
        eventCenterTemplate.doEvents(event);
    }

    @IntrestFor(event = "started")
    public void doStartScheduleTaskEvent(Event event) {
        log.info("PushWindowDataScheduled，从调度中心收到压测任务启动成功事件");
        Object object = event.getExt();
        TaskResult taskBean = (TaskResult)object;
        String taskKey = getTaskKey(taskBean.getSceneId(), taskBean.getTaskId(), taskBean.getCustomerId());
        /*
         * 压测时长 + 预热时长 + 五分钟 7天
         */
        long taskTimeout = 7L * 24 * 60 * 60;
        Map<String, Object> extMap = taskBean.getExtendMap();
        List<String> refList = Lists.newArrayList();
        if (MapUtils.isNotEmpty(extMap)) {
            Object businessActivityBindRef = extMap.get("businessActivityBindRef");
            if (businessActivityBindRef instanceof List) {
                CollUtil.newArrayList(businessActivityBindRef)
                    .forEach(t -> refList.add(t == null ? null : t.toString()));
            }
        }
        ArrayList<String> transation = new ArrayList<>(refList);
        transation.add("all");
        String redisKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, taskKey);
        redisTemplate.opsForValue().set(redisKey, transation, taskTimeout, TimeUnit.SECONDS);
        log.info("PushWindowDataScheduled Create Redis Key = {}, expireDuration={}min, refList={} Success....",
            redisKey, taskTimeout, refList);
    }

    // todo 没有用到
    @IntrestFor(event = "stop")
    public void doStopTaskEvent(Event event) {
        TaskConfig taskConfig = (TaskConfig)event.getExt();
        delTask(taskConfig.getSceneId(), taskConfig.getTaskId(), taskConfig.getCustomerId());
    }

    /**
     * 删除 拉取数据
     */
    @IntrestFor(event = "finished")
    public void doDeleteTaskEvent(Event event) {
        try {
            log.info("通知PushWindowDataScheduled模块，从调度中心收到压测任务结束事件");
            TaskResult taskResult = (TaskResult)event.getExt();
            delTask(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getCustomerId());
        } catch (Exception e) {
            log.error("【PushWindowDataScheduled】处理finished事件异常={}", e.getMessage(), e);
        }
    }

    private void delTask(Long sceneId, Long reportId, Long customerId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null || reportResult.getStatus() == 0) {
            log.info("删除收集数据key时，报告还未生成，sceneId:{},reportId:{}", sceneId, reportId);
            return;
        }
        if (null != sceneId && null != reportId) {
            String taskKey = getTaskKey(sceneId, reportId, customerId);
            redisTemplate.delete(String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY, taskKey));
        }
    }

    private Long getMetricsMinTimeWindow(Long sceneId, Long reportId, Long customerId) {
        try {
            String measurement = InfluxDBUtil.getMetricsMeasurement(sceneId, reportId, customerId);
            ResponseMetrics metrics = influxWriter.querySingle(new SQL()
                .SELECT("*").FROM(measurement).WHERE("time > 0")
                .ORDER_BY("time ASC").LIMIT(1).toString(), ResponseMetrics.class);
            if (null != metrics) {return CollectorUtil.getTimeWindowTime(metrics.getTime());}
        } catch (Throwable e) {log.error("查询失败", e);}
        return null;
    }

    private List<ResponseMetrics> queryMetrics(Long sceneId, Long reportId, Long customerId, Long timeWindow) {
        try {
            //查询引擎上报数据时，通过时间窗向前5s来查询，(0,5]
            String measurement = InfluxDBUtil.getMetricsMeasurement(sceneId, reportId, customerId);
            SQL sql = new SQL().SELECT("*").FROM(measurement);
            if (timeWindow != null) {
                long endTime = TimeUnit.NANOSECONDS.convert(timeWindow, TimeUnit.MILLISECONDS);
                long startTime = endTime - TimeUnit.NANOSECONDS.convert(CollectorConstants.SEND_TIME, TimeUnit.SECONDS);
                sql.WHERE("time > " + startTime).WHERE("time <= " + endTime);
                List<ResponseMetrics> query = influxWriter.query(sql.toString(), ResponseMetrics.class);
                log.info("汇总查询日志：sceneId:{},sql:{},查询结果数量:{}", sceneId, sql, query == null ? "null" : query.size());
                return query;
            } else {
                timeWindow = getMetricsMinTimeWindow(sceneId, reportId, customerId);
                if (timeWindow != null) {return queryMetrics(sceneId, reportId, customerId, timeWindow);}
            }
        } catch (Throwable e) {log.error("查询失败", e);}
        return new ArrayList<>();
    }

    /**
     * 获取当前未完成统计的最小时间窗口
     */
    private Long getWorkingPressureMinTimeWindow(Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
            PressureOutput pressure = influxWriter.querySingle(
                "select * from " + measurement + " where status=0 order by time asc limit 1", PressureOutput.class);
            if (null != pressure) {
                timeWindow = pressure.getTime();
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    /**
     * 获取当前统计的最大时间的下一个窗口窗口
     */
    private Long getPressureMaxTimeNextTimeWindow(Long sceneId, Long reportId, Long customerId) {
        Long timeWindow = null;
        try {
            String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
            PressureOutput pressure = influxWriter.querySingle(
                "select * from " + measurement + " where status=1 order by time desc limit 1", PressureOutput.class);
            if (null != pressure) {
                timeWindow = CollectorUtil.getNextTimeWindow(pressure.getTime());
            }
        } catch (Throwable e) {
            log.error("查询失败", e);
        }
        return timeWindow;
    }

    /**
     * 聚合指标数据
     *
     * @param sceneId    场景主键
     * @param reportId   报告主键
     * @param customerId 租户主键
     * @param podNum     pod数量
     * @param endTime    停止时间
     * @param timeWindow 时间窗口
     * @param nodes      脚本解析结果
     * @return 真实操作的时间窗口
     */
    private Long reduceMetrics(Long sceneId, Long reportId, Long customerId, Integer podNum, long endTime,
        Long timeWindow, List<ScriptNode> nodes) {
        String logPre = String.format("reduceMetrics %s-%s-%s:%s", sceneId, reportId, customerId,
            DateUtil.showTime(timeWindow));
        log.info(logPre + " start!");
        //如果时间窗口为空
        if (null == timeWindow) {
            //则通过当前压测统计表的未完成记录时间进行统计（数据统计有缺失的为未完成）
            timeWindow = getWorkingPressureMinTimeWindow(sceneId, reportId, customerId);
            //如果不存在当前未完成记录时间
            if (timeWindow == null) {
                //则根据最新统计记录时间获取下一个时间窗口
                timeWindow = getPressureMaxTimeNextTimeWindow(sceneId, reportId, customerId);
            }
        }
        //如果当前处理的时间窗口已经大于当结束时间窗口，则退出
        if (timeWindow != null && timeWindow > endTime) {
            log.info("{} return 1!timeWindow={}, endTime={}",
                logPre, DateUtil.showTime(timeWindow), DateUtil.showTime(endTime));
            return timeWindow;
        }
        //timeWindow如果为空，则获取全部metrics数据，如果不为空则获取该时间窗口的数据
        List<ResponseMetrics> metricsList = queryMetrics(sceneId, reportId, customerId, timeWindow);
        if (CollUtil.isEmpty(metricsList)) {
            log.info("{}, timeWindow={} ， metrics 是空集合!", logPre, DateUtil.showTime(timeWindow));
            return timeWindow;
        }
        log.info("{} queryMetrics timeWindow={}, endTime={}, metricsList.size={}",
            logPre, DateUtil.showTime(timeWindow), DateUtil.showTime(endTime), metricsList.size());
        if (null == timeWindow) {
            timeWindow = metricsList.stream().filter(Objects::nonNull)
                .map(t -> CollectorUtil.getTimeWindowTime(t.getTime()))
                .filter(l -> l > 0)
                .findFirst()
                .orElse(endTime);
        }
        //如果当前处理的时间窗口已经大于结束时间窗口，则退出
        if (timeWindow > endTime) {
            log.info("{} return 3!timeWindow={}, endTime={}",
                logPre, DateUtil.showTime(endTime), DateUtil.showTime(timeWindow));
            return timeWindow;
        }

        List<String> transactions = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getTransaction)
            .filter(StringUtils::isNotBlank)
            //过滤掉控制器
            .filter(t -> !this.isController(t, nodes))
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(transactions)) {
            log.info("{} return 4!transactions is empty!", logPre);
            return timeWindow;
        }

        String measurement = InfluxDBUtil.getMeasurement(sceneId, reportId, customerId);
        long time = timeWindow;

        List<PressureOutput> results = transactions.stream().filter(StringUtils::isNotBlank)
            .map(s -> this.filterByTransactionAndPodNo(metricsList, s))
            .filter(CollectionUtils::isNotEmpty)
            .map(l -> this.toPressureOutput(l, podNum, time))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        List<PressureOutput> slaList = new ArrayList<>(results);
        //统计没有回传的节点数据
        if (CollectionUtils.isNotEmpty(nodes)) {
            //控制器统计
            List<ScriptNode> controllerNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.CONTROLLER, nodes);
            if (CollectionUtils.isNotEmpty(controllerNodes)) {
                controllerNodes.stream().filter(Objects::nonNull)
                    //过滤掉已经有数据的控制器
                    .filter(c -> !transactions.contains(c.getXpathMd5()))
                    .forEach(c -> this.summaryNodeMetrics(c, podNum, time, results, slaList));
            }
            //线程组统计
            List<ScriptNode> threadGroupNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.THREAD_GROUP, nodes);
            if (CollectionUtils.isNotEmpty(threadGroupNodes)) {
                threadGroupNodes.stream().filter(Objects::nonNull)
                    .filter(n -> !transactions.contains(n.getXpathMd5()))
                    .forEach(n -> this.summaryNodeMetrics(n, podNum, time, results, slaList));
            }
            //测试计划统计
            List<ScriptNode> testPlanNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.TEST_PLAN, nodes);
            if (CollectionUtils.isNotEmpty(testPlanNodes)) {
                testPlanNodes.stream().filter(Objects::nonNull)
                    .filter(t -> !transactions.contains(t.getXpathMd5()))
                    .forEach(t -> this.summaryNodeMetrics(t, podNum, time, results, slaList));
            }
        }
        //如果是老版本的，统计ALL
        else {
            results.add(createPressureOutput(results, time, podNum,
                ReportConstants.ALL_BUSINESS_ACTIVITY, ReportConstants.ALL_BUSINESS_ACTIVITY));
        }
        //sla处理
        try {
            List<SendMetricsEvent> sendMetricsEventList = getSendMetricsEventList(sceneId, reportId, customerId,
                timeWindow, slaList);
            //未finish，发事件
            String existKey = String.format(CollectorConstants.REDIS_PRESSURE_TASK_KEY,
                getTaskKey(sceneId, reportId, customerId));
            if (Boolean.TRUE.equals(redisTemplate.hasKey(existKey))) {
                //排除控制器和理线程组，只处理采样器和测试计划
                sendMetricsEventList.stream().filter(Objects::nonNull)
                    .forEach(this::sendMetrics);
            }
        } catch (Exception e) {
            log.error(
                "【collector metric】【error-sendMetricsEvents】 write influxDB time : {} sceneId : {}, reportId : "
                    + "{},customerId : {}, error:{}",
                timeWindow, sceneId, reportId, customerId, e.getMessage());
        }
        results.stream().filter(Objects::nonNull)
            .map(p -> InfluxDBUtil.toPoint(measurement, time, p))
            .forEach(influxWriter::insert);

        log.info(logPre + " finished!timeWindow=" + DateUtil.showTime(timeWindow) + ", endTime=" + DateUtil
            .showTime(endTime));
        return timeWindow;
    }

    private boolean isController(String transaction, List<ScriptNode> nodes) {
        List<ScriptNode> nodeList = JmxUtil.getScriptNodeByType(NodeTypeEnum.CONTROLLER, nodes);
        if (CollectionUtils.isNotEmpty(nodeList)) {
            List<String> controllerTransactions = nodeList.stream().filter(Objects::nonNull)
                .map(ScriptNode::getXpathMd5)
                .collect(Collectors.toList());
            return controllerTransactions.contains(transaction);
        } else {
            return false;
        }
    }

    private PressureOutput createPressureOutput(List<PressureOutput> results, long time,
        int podNum, String transaction, String testName) {
        int count = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getCount)
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .sum();
        int failCount = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getFailCount)
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .sum();
        int saCount = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSaCount)
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .sum();
        double sa = NumberUtil.getPercentRate(saCount, count);
        double successRate = NumberUtil.getPercentRate(count - failCount, count);
        long sendBytes = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSentBytes)
            .filter(Objects::nonNull)
            .mapToLong(l -> l)
            .sum();
        long receiveBytes = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getReceivedBytes)
            .filter(Objects::nonNull)
            .mapToLong(l -> l)
            .sum();

        long sumRt = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSumRt)
            .filter(Objects::nonNull)
            .mapToLong(l -> l)
            .sum();

        double avgRt = NumberUtil.getRate(sumRt, count);

        double maxRt = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getMaxRt)
            .filter(Objects::nonNull)
            .mapToDouble(d -> d)
            .max()
            .orElse(0);

        double minRt = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getMinRt)
            .filter(Objects::nonNull)
            .mapToDouble(d -> d)
            .min()
            .orElse(0);

        int activeThreads = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getActiveThreads)
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .max()
            .orElse(0);
        double avgTps = NumberUtil.getRate(count, CollectorConstants.SEND_TIME);
        List<String> percentData = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getSaPercent)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String percentSa = calculateSaPercent(percentData);
        int podNos = results.stream().filter(Objects::nonNull)
            .map(PressureOutput::getDataNum)
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .findFirst()
            .orElse(0);
        double dataRate = NumberUtil.getPercentRate(podNos, podNum, 100d);
        int status = podNos < podNum ? 0 : 1;
        return new PressureOutput()
            .setTime(time).setCount(count).setTransaction(transaction).setFailCount(failCount)
            .setSaCount(saCount).setSa(sa).setSuccessRate(successRate).setSentBytes(sendBytes)
            .setReceivedBytes(receiveBytes).setSumRt(sumRt).setAvgRt(avgRt).setMaxRt(maxRt)
            .setMinRt(minRt).setActiveThreads(activeThreads).setAvgTps(avgTps).setStatus(status)
            .setDataNum(podNos).setSaPercent(percentSa).setDataRate(dataRate).setTestName(testName);
    }

    /**
     * 单个时间窗口数据，根据transaction过滤，并且每个pod只取1条数据
     */
    private List<ResponseMetrics> filterByTransactionAndPodNo(List<ResponseMetrics> metricses, String transaction) {
        if (CollectionUtils.isEmpty(metricses)) {
            return metricses;
        }
        List<String> pods = Lists.newArrayList();
        return metricses.stream().filter(Objects::nonNull)
            .filter(m -> transaction.equals(m.getTransaction()))
            .filter(m -> !pods.contains(m.getPodNo()))
            .map(m -> {
                pods.add(m.getPodNo());
                return m;
            })
            .collect(Collectors.toList());
    }

    /**
     * 统计节点中没有上报的测试计划、线程组、控制器的请求信息
     *
     * @param targetNode 需要统计目标节点
     * @param podNum     podNum
     * @param time       时间窗口
     * @param data       经过统计计算的metrics数据
     */
    private void summaryNodeMetrics(ScriptNode targetNode, int podNum, Long time, List<PressureOutput> data, List<PressureOutput> slaList) {
        String transaction = targetNode.getXpathMd5();
        String testName = targetNode.getTestName();
        List<ScriptNode> childSamplers = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, targetNode.getChildren());
        Map<String, List<PressureOutput>> dataMap = data.stream().collect(
            Collectors.groupingBy(PressureOutput::getTransaction));
        List<PressureOutput> tmpData = new ArrayList<>();
        List<String> samplerTransactions = childSamplers.stream().filter(Objects::nonNull)
            .map(ScriptNode::getXpathMd5)
            .collect(Collectors.toList());
        for (Map.Entry<String, List<PressureOutput>> entry : dataMap.entrySet()) {
            if (samplerTransactions.contains(entry.getKey())) {
                tmpData.addAll(entry.getValue());
            }
        }
        PressureOutput pressureOutput = createPressureOutput(tmpData, time, podNum, transaction, testName);
        data.add(pressureOutput);
        if (CollectionUtils.isNotEmpty(slaList) && targetNode.getType() == NodeTypeEnum.TEST_PLAN) {
            slaList.add(pressureOutput);
        }
    }

    /**
     * 实时数据统计
     */
    private PressureOutput toPressureOutput(List<ResponseMetrics> metricsList, Integer podNum, long time) {
        if (CollUtil.isEmpty(metricsList)) {return null;}
        String transaction = metricsList.get(0).getTransaction();
        String testName = metricsList.get(0).getTestName();
        PressureOutput result = new PressureOutput()
            .setTime(time).setTestName(testName).setTransaction(transaction)
            .setCount(0).setSumRt(0L).setSaCount(0).setDataNum(0).setFailCount(0)
            .setMaxRt(Double.MIN_VALUE).setMinRt(Double.MAX_VALUE)
            .setActiveThreads(0).setSentBytes(0L).setReceivedBytes(0L);
        // 根据pod编号进行分组
        Map<String, List<ResponseMetrics>> podGroupData =
            metricsList.stream().collect(Collectors.groupingBy(ResponseMetrics::getPodNum));
        for (Map.Entry<String, List<ResponseMetrics>> entry : podGroupData.entrySet()) {
            List<ResponseMetrics> metrics = entry.getValue();
            if (CollUtil.isNotEmpty(metrics)) {
                // 请求总数|总RT|SA总数|请求失败总数|发送字节|响应字节
                // 都是简单的求和
                result.setCount(result.getCount() + NumberUtil.sum(metrics, ResponseMetrics::getCount));
                result.setSumRt(result.getSumRt() + NumberUtil.sumLong(metrics, ResponseMetrics::getSumRt));
                result.setSaCount(result.getSaCount() + NumberUtil.sum(metrics, ResponseMetrics::getSaCount));
                result.setFailCount(result.getFailCount() + NumberUtil.sum(metrics, ResponseMetrics::getFailCount));
                result.setSentBytes(result.getSentBytes() + NumberUtil.sumLong(metrics, ResponseMetrics::getSentBytes));
                result.setReceivedBytes(result.getReceivedBytes() + NumberUtil.sumLong(metrics, ResponseMetrics::getReceivedBytes));
                // 最大RT|最小RT
                // 取极值
                result.setMaxRt(Math.max(result.getMaxRt(), NumberUtil.maxDouble(metrics, ResponseMetrics::getMaxRt)));
                result.setMinRt(Math.min(result.getMinRt(), NumberUtil.minDouble(metrics, ResponseMetrics::getMinRt)));
                // 活跃线程数(并发数)
                // pod内计算平均值后累加
                //  1. 计算出当前pod的并发数平均值
                double activeThread = cn.hutool.core.util.NumberUtil.div(NumberUtil.sum(metrics, ResponseMetrics::getActiveThreads), metrics.size());
                //  2. 累加
                result.setActiveThreads(result.getActiveThreads() + (int)activeThread);
                // dataNumber
                if (CharSequenceUtil.isNotBlank(entry.getKey())) {result.setDataNum(result.getDataNum() + 1);}
            }
        }
        // 数据收集的是否完整
        result.setStatus(result.getDataNum().equals(podNum) ? 1 : 0);
        // SA       =   SA总数 / 请求总数
        result.setSa(NumberUtil.getPercentRate(result.getSaCount(), result.getCount()));
        // 成功率      =   ( 请求总数 - 请求失败总数 ) / 请求总数
        result.setSuccessRate(NumberUtil.getPercentRate(result.getCount() - result.getFailCount(), result.getCount()));
        // 平均RT     =   总RT / 请求总数
        result.setAvgRt(NumberUtil.getRate(result.getSumRt(), result.getCount()));
        // 平均TPS    =   请求总数 / 聚合窗口大小
        result.setAvgTps(NumberUtil.getRate(result.getCount(), CollectorConstants.SEND_TIME));
        // 数据采集量    =   数据来源的pod个数 / 总pod个数
        result.setDataRate(NumberUtil.getPercentRate(result.getDataNum(), podNum, 100d));
        // 百分位
        List<String> percentDataList = metricsList.stream().filter(Objects::nonNull)
            .map(ResponseMetrics::getPercentData)
            .filter(CharSequenceUtil::isNotBlank)
            .collect(Collectors.toList());
        result.setSaPercent(calculateSaPercent(percentDataList));
        return result;
    }

    private void finishPushData(Long sceneId, Long reportId, Long customerId, Integer podNum, Long timeWindow,
        long endTime, List<ScriptNode> nodes) {
        String taskKey = getPressureTaskKey(sceneId, reportId, customerId);
        String last = String.valueOf(redisTemplate.opsForValue().get(last(taskKey)));
        long nowTimeWindow = CollectorUtil.getTimeWindowTime(System.currentTimeMillis());
        log.info("finishPushData {}-{}-{} last={}, timeWindow={}, endTime={}, now={}", sceneId, reportId, customerId,
            last,
            DateUtil.showTime(timeWindow), DateUtil.showTime(endTime), DateUtil.showTime(nowTimeWindow));

        ReportResult report = reportDao.selectById(reportId);
        if (report != null && report.getEndTime() != null) {
            endTime = Math.min(endTime, report.getEndTime().getTime());
        }

        if (ScheduleConstants.LAST_SIGN.equals(last) || (null != timeWindow && timeWindow > endTime)) {
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, customerId);
            // 只需触发一次即可
            String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
            Long eTime = (Long)redisTemplate.opsForValue().get(endTimeKey);
            if (null != eTime) {
                log.info("触发手动收尾操作，当前时间窗口：{},结束时间窗口：{},", DateUtil.showTime(timeWindow), DateUtil.showTime(eTime));
                endTime = Math.min(endTime, eTime);
            } else {
                eTime = endTime;
            }
            long endTimeWindow = CollectorUtil.getTimeWindowTime(endTime);
            log.info("触发收尾操作，当前时间窗口：{},结束时间窗口：{},", DateUtil.showTime(timeWindow), DateUtil.showTime(endTimeWindow));
            // 比较 endTime timeWindow
            // 如果结束时间 小于等于当前时间，数据不用补充，
            // 如果结束时间 大于 当前时间，需要补充期间每5秒的数据 延后5s
            while (isSaveLastPoint && timeWindow <= endTimeWindow && timeWindow <= nowTimeWindow) {
                timeWindow = reduceMetrics(sceneId, reportId, customerId, podNum, eTime, timeWindow, nodes);
                timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
            }
            log.info("本次压测{}-{}-{},push data 完成", sceneId, reportId, customerId);
            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, customerId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
            log.info("---> 本次压测{}-{}-{}完成，已发送finished事件！<------", sceneId, reportId, customerId);
        }
        // 超时自动检修，强行触发关闭
        forceClose(taskKey, nowTimeWindow, sceneId, reportId, customerId);
    }

    /**
     * 实时数据统计
     */
    public void pushData() {
        ReportQueryParam param = new ReportQueryParam().setStatus(0).setIsDel(0);
        List<ReportResult> results = reportDao.queryReportList(param);
        if (CollectionUtils.isEmpty(results)) {log.info("没有需要统计的报告！");}
        // 开始统计
        else {
            List<Long> reportIds = results.stream()
                .filter(Objects::nonNull)
                .map(ReportResult::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            log.info("找到需要统计的报告：{}", reportIds);
            results.forEach(r -> Executors.execute(() -> {
                Long sceneId = r.getSceneId();
                Long reportId = r.getId();
                Long customerId = r.getCustomerId();
                String lockKey = String.format("pushData:%s:%s:%s", sceneId, reportId, customerId);
                if (!lock(lockKey, "1")) {return;}
                try {
                    List<ScriptNode> nodes = JsonUtil.parseArray(r.getScriptNodeTree(), ScriptNode.class);
                    SceneManageWrapperOutput scene = sceneManageService.getSceneManage(sceneId, null);
                    if (scene != null) {
                        //结束时间取开始压测时间+总测试时间+3分钟， 3分钟富裕时间，给与pod启动和压测引擎启动延时时间
                        long endTime = getEndTime(scene, r);
                        int podNum = scene.getIpNum();
                        long nowTimeWindow = CollectorUtil.getNowTimeWindow();
                        long breakTime = Math.min(endTime, nowTimeWindow);
                        Long timeWindow = -1L;
                        while (timeWindow <= breakTime) {
                            //获取最后一条数据的时间，如果最后一条回传数据的时间比当前时间少3分钟以上，则认为引擎不会继续回传数据了，结束掉,设置endTime为最后一条数据的时间
                            //if(ifReportOutOfTime(sceneId, reportId, customerId,r)){
                            //    log.error("3分钟未收到压测引擎回传数据或上条数据已超过三分钟，停止数据收集，场景ID:{},报告ID:{}",sceneId,reportId);
                            //    break;
                            //}
                            if (timeWindow <= 0) {timeWindow = null;}
                            timeWindow = reduceMetrics(sceneId, reportId, customerId, podNum, breakTime, timeWindow, nodes);
                            if (null == timeWindow) {
                                timeWindow = nowTimeWindow;
                                break;
                            }
                            timeWindow = CollectorUtil.getNextTimeWindow(timeWindow);
                        }
                        if (r.getEndTime() != null) {
                            // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
                            sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, customerId)
                                .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                                .updateEnum(SceneManageStatusEnum.STOP)
                                .build());
                        }
                        // 结束报告
                        finishPushData(sceneId, reportId, customerId, podNum, timeWindow, endTime, nodes);
                    }
                    // 没有找到场景
                    else {log.info("no such scene manager!sceneId=" + sceneId);}
                } catch (Throwable t) {log.error("pushData error!", t);} finally {unlock(lockKey, "0");}
            }));
        }
    }

    long getEndTime(SceneManageWrapperOutput scene, ReportResult report) {
        long endTime = TimeUnit.MINUTES.toMillis(3L);
        if (null != report.getStartTime()) {
            endTime += report.getStartTime().getTime();
        } else if (null != report.getGmtCreate()) {
            endTime += report.getGmtCreate().getTime();
        }
        if (null != scene.getTotalTestTime()) {
            endTime += TimeUnit.SECONDS.toMillis(scene.getTotalTestTime());
        } else if (null != scene.getPressureTestSecond()) {
            endTime += TimeUnit.SECONDS.toMillis(scene.getPressureTestSecond());
        }
        return endTime;
    }

    /**
     * 每五秒执行一次
     * 每次从redis中取10秒前的数据
     */
    @Async("collectorSchedulerPool")
    @Scheduled(cron = "0/5 * * * * ? ")
    public void pushDataScheduled() {
        if (Boolean.TRUE.equals(schedulingEnabled)) {pushData();}
    }

    /**
     * 超时自动检修，强行触发关闭
     *
     * @param taskKey    任务key
     * @param timeWindow 数据窗口
     */
    private void forceClose(String taskKey, Long timeWindow, Long sceneId, Long reportId, Long customerId) {
        Long forceTime = (Long)Optional.ofNullable(redisTemplate.opsForValue().get(forceCloseTime(taskKey))).orElse(0L);
        if (forceTime > 0 && timeWindow >= forceTime) {
            log.info("本次压测{}-{}-{}:触发超时自动检修，强行触发关闭，超时延迟时间-{}，触发时间-{}",
                sceneId, reportId, customerId, forceTime, timeWindow);

            log.info("场景[{}]压测任务已完成,将要开始更新报告{}", sceneId, reportId);
            // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
            SceneManageResult sceneManage = sceneManageDAO.getSceneById(sceneId);
            //如果是强制停止 不需要更新
            log.info("finish scene {}, state :{}", sceneId, Optional.ofNullable(sceneManage)
                .map(SceneManageResult::getType)
                .map(SceneManageStatusEnum::getSceneManageStatusEnum)
                .map(SceneManageStatusEnum::getDesc).orElse("未找到场景"));
            if (sceneManage != null && !SceneManageStatusEnum.FORCE_STOP.getValue().equals(sceneManage.getType())) {
                sceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, customerId)
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                    .updateEnum(SceneManageStatusEnum.STOP)
                    .build());
            }
            // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告
            Event event = new Event();
            event.setEventName("finished");
            event.setExt(new TaskResult(sceneId, reportId, customerId));
            eventCenterTemplate.doEvents(event);
            redisTemplate.delete(last(taskKey));
        }
    }

    /**
     * 计算sa
     */
    private String calculateSaPercent(List<String> percentDatas) {
        List<Map<Integer, RtDataOutput>> percentMapList = percentDatas.stream().filter(StringUtils::isNotBlank)
            .map(DataUtils::parseToPercentMap)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(percentMapList)) {
            return null;
        }
        //请求总数
        int total = percentMapList.stream().filter(Objects::nonNull)
            .map(m -> m.get(100))
            .filter(Objects::nonNull)
            .mapToInt(RtDataOutput::getHits)
            .sum();

        //所有rt按耗时排序
        List<RtDataOutput> rtDatas = percentMapList.stream().filter(Objects::nonNull)
            .map(t -> {
                DataUtils.percentMapRemoveDuplicateHits(t);
                return t.values();
            })
            .filter(CollectionUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(RtDataOutput::getTime))
            .collect(Collectors.toList());

        Map<Integer, RtDataOutput> result = new HashMap<>(100);
        //计算逻辑
        //每个百分点的目标请求数，如果统计达标，进行下一个百分点的统计，如果tong ji
        for (int i = 1; i <= 100; i++) {
            int hits = 0;
            int time = 0;
            double need = total * i / 100d;
            for (RtDataOutput d : rtDatas) {
                if (hits < need || d.getTime() <= time) {
                    hits += d.getHits();
                    if (d.getTime() > time) {
                        time = d.getTime();
                    }
                }
            }
            result.put(i, new RtDataOutput(hits, time));
        }
        return DataUtils.percentMapToString(result);
    }

    private List<SendMetricsEvent> getSendMetricsEventList(Long sceneId, Long reportId, Long customerId,
        long timeWindow, List<PressureOutput> pressureOutputs) {
        return pressureOutputs.stream().filter(Objects::nonNull)
            .map(output -> {
                SendMetricsEvent metrics = new SendMetricsEvent();
                metrics.setTransaction(output.getTransaction());
                metrics.setCount(output.getCount());
                metrics.setFailCount(output.getFailCount());
                metrics.setAvgTps(output.getAvgTps());
                metrics.setAvgRt(output.getAvgRt());
                metrics.setSa(output.getSa());
                metrics.setMaxRt(output.getMaxRt());
                metrics.setMinRt(output.getMinRt());
                metrics.setSuccessRate(output.getSuccessRate());
                metrics.setTimestamp(timeWindow);
                metrics.setReportId(reportId);
                metrics.setSceneId(sceneId);
                metrics.setCustomerId(customerId);
                return metrics;
            }).collect(Collectors.toList());
    }

}
