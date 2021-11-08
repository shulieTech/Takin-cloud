package io.shulie.takin.cloud.entrypoint.controller.collector;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.cloud.common.utils.IPUtils;
import io.shulie.takin.cloud.common.utils.UrlUtil;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.common.bean.collector.Constants;
import io.shulie.takin.cloud.common.bean.collector.EventMetrics;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.collector.collector.CollectorService;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-17 17:20
 */
@Slf4j
@RestController
@Api(tags = "接受压测引擎参数")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_COLLECTOR)
public class CollectorApplication {

    @Resource
    private CollectorService collectorService;
    @Resource
    private RedisClientUtils redisClientUtils;

    @Value("${script.pre.match:true}")
    private boolean scriptPreMatch;

    @ApiOperation("接收事件和压测数据")
    @RequestMapping(EntrypointUrl.METHOD_COLLECTOR_RECEIVE)
    public ResponseEntity<String> receive(@ApiParam("场景id") @RequestParam("sceneId") Long sceneId,
        @ApiParam("报告id") @RequestParam("reportId") Long reportId,
        @ApiParam("租户id") @RequestParam(value = "tenantId", required = false) Long tenantId,
        @ApiParam("事件或数据参数") @RequestBody List<Map<?, ?>> metrics,
        HttpServletRequest request) {
        try {
            if (sceneId == null || reportId == null) {
                return ResponseEntity.ok("唯一标示不能为空");
            }
            if (null == metrics || metrics.size() < 1) {
                return ResponseEntity.ok("metrics数据为空");
            }
            // 分类
            List<ResponseMetrics> responseMetrics = metrics.stream().filter(Objects::nonNull)
                .filter(metric -> null != metric.get("type"))
                .filter(metric -> Constants.METRICS_TYPE_RESPONSE.equals(metric.get("type")))
                .map(GsonUtil::gsonToString)
                .map(s -> GsonUtil.gsonToBean(s, ResponseMetrics.class))
                // TODO 确认方案以及发生原因
                .filter(Objects::nonNull)
                .peek(t -> t.setPodNo(t.getPodNo() == null ? t.getPodNum() : t.getPodNo()))
                .collect(Collectors.toList());
            List<EventMetrics> eventMetrics = metrics.stream().filter(Objects::nonNull)
                .filter(metric -> null != metric.get("type"))
                .filter(metric -> Constants.METRICS_TYPE_EVENTS.equals(metric.get("type")))
                .map(GsonUtil::gsonToString)
                .map(s -> GsonUtil.gsonToBean(s, EventMetrics.class))
                .collect(Collectors.toList());
            culTransaction(responseMetrics, sceneId, reportId, tenantId);
            long time = System.currentTimeMillis();
            if (responseMetrics.size() > 0) {
                long timestamp = responseMetrics.get(0).getTimestamp();
                log.debug("【收集器调度-debug】{}-{}-{}:接受到的数据:{}", sceneId, reportId, tenantId, GsonUtil.gsonToString(responseMetrics));
                log.info("【收集器调度-metrics】{}-{}-{}: 接收到的数据:{}条,时间范围:{},已耗时:{}",
                    sceneId, reportId, tenantId, responseMetrics.size(), timestamp, (System.currentTimeMillis() - time));

                collectorService.collector(sceneId, reportId, tenantId, responseMetrics);
                collectorService.statisticalIp(sceneId, reportId, tenantId, timestamp, IPUtils.getIP(request));
            }
            if (eventMetrics.size() > 0) {
                log.info("Collector-metrics-event】{}-{}-{}:{}", sceneId, reportId, tenantId,
                    GsonUtil.gsonToString(eventMetrics));
                collectorService.verifyEvent(sceneId, reportId, tenantId, eventMetrics);
            }
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：接收压测引擎回传数据异常 --> 【Collector-metrics-Error】接收数据信息，异常信息: {}",
                TakinCloudExceptionEnum.TASK_RUNNING_RECEIVE_PT_DATA_ERROR, e);
            return ResponseEntity.ok(e.getMessage());
        }
    }

    private void culTransaction(List<ResponseMetrics> responseMetrics, Long sceneId, Long reportId, Long tenantId) {
        //后置匹配处理逻辑，如果是前置匹配，不需要处理
        if (!scriptPreMatch && CollectionUtils.isNotEmpty(responseMetrics)) {
            String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(sceneId, reportId, tenantId);
            Object activityRefMapObj = redisClientUtils.hmget(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.ACTIVITY_REF_MAP);
            Map<String, String> activityRefMap = activityRefMapObj == null ? new HashMap<>(0) :
                JsonHelper.json2Map(activityRefMapObj.toString(), String.class, String.class);
            int oldSize = activityRefMap.size();

            responseMetrics.forEach(responseMetric -> {
                //特殊业务类型，不做处理
                if ("all".equals(responseMetric.getTransaction())) {
                    return;
                }
                if (activityRefMap.containsKey(responseMetric.getTransaction())) {
                    responseMetric.setTransaction(activityRefMap.get(responseMetric.getTransaction()));
                    return;
                }
                String activityRef = getActivityRef(engineInstanceRedisKey, responseMetric.getTransaction());
                if (activityRef != null) {
                    activityRefMap.put(responseMetric.getTransaction(), activityRef);
                }
            });

            //关联关系被补充，设置进map
            if (oldSize != activityRefMap.size()) {
                redisClientUtils.hmset(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.ACTIVITY_REF_MAP, JsonHelper.bean2Json(activityRefMap));
            }
        }
    }

    private String getActivityRef(String engineInstanceRedisKey, String transaction) {
        Object activityRefListObj = redisClientUtils.hmget(engineInstanceRedisKey, PressureInstanceRedisKey.SecondRedisKey.ACTIVITY_REFS);
        if (activityRefListObj != null) {
            List<String> activityRefs = JsonHelper.json2List(activityRefListObj.toString(), String.class);
            //for循环从List取值
            for (String activityRef : activityRefs) {
                if (UrlUtil.checkEqual(transaction, activityRef)) {
                    return activityRef;
                }
            }
        }
        return null;
    }
}
