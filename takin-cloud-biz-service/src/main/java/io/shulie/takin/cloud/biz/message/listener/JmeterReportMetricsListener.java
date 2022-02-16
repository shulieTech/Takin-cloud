package io.shulie.takin.cloud.biz.message.listener;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import io.shulie.jmeter.tool.redis.domain.TkMessage;
import io.shulie.takin.cloud.common.bean.collector.Constants;
import io.shulie.takin.cloud.common.bean.collector.EventMetrics;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: liyuanba
 * @Date: 2022/2/16 11:51 上午
 */
@Slf4j
@Component
public class JmeterReportMetricsListener extends AbstractJmeterReportListener {

    @Override
    public List<String> getTags() {
        return Lists.newArrayList("metrics");
    }

    @Override
    public boolean receive(TkMessage message) {
        log.info("接收到数据：message="+ JsonUtil.toJson(message));
        if (StringUtils.isBlank(message.getKey())) {
            return true;
        }
        String[] keys = message.getContent().split("\\|");
        Long taskId = null;
        PressureSceneEnum sceneType = null;
        if (keys.length >= 4) {
            taskId = NumberUtils.toLong(keys[1]);
            sceneType = PressureSceneEnum.value(NumberUtils.toInt(keys[3], -1));
        }
        if (sceneType == PressureSceneEnum.INSPECTION_MODE) {
            return true;
        }
        List<Map<String, Object>> metrics = JsonUtil.parseObject(message.getContent(), new TypeReference<List<Map<String, Object>>>(){});
        if (CollectionUtils.isNotEmpty(metrics)) {
            // 分类
            List<ResponseMetrics> responseMetrics = metrics.stream().filter(Objects::nonNull)
                    .filter(metric -> null != metric.get("type"))
                    .filter(metric -> Constants.METRICS_TYPE_RESPONSE.equals(metric.get("type")))
                    .map(JsonUtil::toJson)
                    .map(s -> JsonUtil.parseObject(s, ResponseMetrics.class))
                    // 原因:引擎不匹配，以下做法可兼容
                    .filter(Objects::nonNull)
                    .peek(t -> t.setPodNo(t.getPodNo() == null ? t.getPodNum() : t.getPodNo()))
                    .collect(Collectors.toList());
            List<EventMetrics> eventMetrics = metrics.stream().filter(Objects::nonNull)
                    .filter(metric -> null != metric.get("type"))
                    .filter(metric -> Constants.METRICS_TYPE_EVENTS.equals(metric.get("type")))
                    .map(JsonUtil::toJson)
                    .map(s -> JsonUtil.parseObject(s, EventMetrics.class))
                    .collect(Collectors.toList());
            if (responseMetrics.size() > 0) {
                //todo
            }
            if (eventMetrics.size() > 0) {
                //todo
            }
        }
        return true;
    }
}
