

package io.shulie.takin.cloud.biz.service.sla.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.dao.scenemanage.TWarnDetailMapper;
import com.pamirs.takin.entity.domain.entity.scenemanage.WarnDetail;
import io.shulie.takin.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.biz.event.SlaPublish;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.output.scenemanage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.sla.SlaService;
import io.shulie.takin.cloud.biz.utils.SlaUtil;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.common.bean.sla.AchieveModel;
import io.shulie.takin.cloud.common.bean.sla.SlaBean;
import io.shulie.takin.cloud.common.constants.Constants;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.common.utils.DateUtil;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageWrapperResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SlaServiceImpl
 * @Description
 * @Author qianshui
 * @Date 2020/4/20 下午4:48
 */
@Service
@Slf4j
public class SlaServiceImpl implements SlaService {

    public static final String SLA_SCENE_KEY = "TRO:SLA:SCENE:KEY";
    public static final String SLA_DESTORY_KEY = "TRO:SLA:DESTORY:KEY";
    public static final String SLA_WARN_KEY = "TRO:SLA:WARN:KEY";
    public static final Long EXPIRE_TIME = 24 * 3600L;
    public static final String PREFIX_TASK = "TRO:SLA:TASK:";
    @Autowired
    private SlaPublish slaPublish;
    @Autowired
    private SceneManageService sceneManageService;
    @Resource
    private TWarnDetailMapper TWarnDetailMapper;
    @Autowired
    private RedisClientUtils redisClientUtils;
    @Autowired
    private ReportService reportService;

    @Override
    public Boolean buildWarn(SendMetricsEvent metricsEvnet) {
        if (StringUtils.isBlank(metricsEvnet.getTransaction())
            || "all".equalsIgnoreCase(metricsEvnet.getTransaction())) {
            return true;
        }
        Long sceneId = metricsEvnet.getSceneId();
        SceneManageWrapperOutput dto;
        try {
            dto = getSceneManageWrapperDTO(sceneId);
            if (dto == null) {
                log.error("异常代码【{}】,异常内容：构建sla异常 --> 未找到压测场景， SendMetricsEvent={}",
                        TakinCloudExceptionEnum.TASK_START_BUILD_SAL,JSON.toJSONString(metricsEvnet));
                return false;
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：构建sla异常 -->  查找到压测场景异常， SendMetricsEvent={}，异常信息:{}",
                    TakinCloudExceptionEnum.TASK_START_BUILD_SAL,JSON.toJSONString(metricsEvnet),e);
            return false;
        }
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivity = dto.getBusinessActivityConfig().stream().filter(
            data -> metricsEvnet.getTransaction().equals(data.getBindRef())).findFirst().orElse(null);

        if (businessActivity == null) {
            log.error("异常代码【{}】,异常内容：构建sla异常 --> 未找到业务活动， SendMetricsEvent={}",
                    TakinCloudExceptionEnum.TASK_START_BUILD_SAL,JSON.toJSONString(metricsEvnet));
            return false;
        }

        Long businessActivityId = businessActivity.getBusinessActivityId();

        doDestory(dto.getId(), metricsEvnet, filterSlaList(businessActivityId, dto.getStopCondition()),businessActivity);

        doWarn(businessActivity, metricsEvnet, filterSlaList(businessActivityId, dto.getWarningCondition()));

        return true;
    }

    @Override
    public void removeMap(Long sceneId) {
        String scene = (String)redisClientUtils.hmget(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (scene == null) {
            return;
        }
        SceneManageWrapperResult dto = JSON.parseObject(scene, SceneManageWrapperResult.class);

        dto.getStopCondition().stream().map(data -> data.getId()).forEach(
            id -> redisClientUtils.hmdelete(SLA_DESTORY_KEY, String.valueOf(id)));
        dto.getWarningCondition().stream().map(data -> data.getId()).forEach(
            id -> redisClientUtils.hmdelete(SLA_WARN_KEY, String.valueOf(id)));
        redisClientUtils.hmdelete(SLA_SCENE_KEY, String.valueOf(sceneId));
        redisClientUtils.delete(PREFIX_TASK + sceneId);
        log.info("清除SLA分析内存配置成功, sceneId={}", sceneId);
    }

    @Override
    public void cacheData(Long sceneId) {
        redisClientUtils.setString(PREFIX_TASK + sceneId, "on", 7, TimeUnit.DAYS);
    }

    private void doDestory(Long sceneId, SendMetricsEvent metricsEvent, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList, SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            SceneSlaRefInput input = new SceneSlaRefInput();
            BeanUtils.copyProperties(dto,input);
            Map<String, Object> conditionMap = SlaUtil.matchCondition(input, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                redisClientUtils.hmdelete(SLA_DESTORY_KEY, String.valueOf(dto.getId()));
                return;
            }
            String object = (String)redisClientUtils.hmget(SLA_DESTORY_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                redisClientUtils.hmset(SLA_DESTORY_KEY, dataMap, EXPIRE_TIME);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                try {
                    ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
                    scheduleStopRequest.setTaskId(metricsEvent.getReportId());
                    scheduleStopRequest.setSceneId(sceneId);
                    // 增加顾客id
                    scheduleStopRequest.setCustomerId(metricsEvent.getCustomerId());
                    Map<String, Object> extendMap = Maps.newHashMap();
                    extendMap.put(Constants.SLA_DESTORY_EXTEND, "SLA发送压测任务终止事件");
                    scheduleStopRequest.setExtend(extendMap);
                    //报告未结束，才通知
                    if (redisClientUtils.hasKey(PREFIX_TASK + metricsEvent.getSceneId())) {
                        // 熔断数据也记录到告警明细中
                        WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                        TWarnDetailMapper.insertSelective(warnDetail);
                        // 记录sla熔断数据
                        UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
                        SlaBean slaBean = new SlaBean();
                        slaBean.setRuleName(dto.getRuleName());
                        slaBean.setBusinessActivity(businessActivityDTO.getBusinessActivityName());
                        slaBean.setRule(warnDetail.getWarnContent());
                        slaDataInput.setReportId(scheduleStopRequest.getTaskId());
                        slaDataInput.setSlaBean(slaBean);
                        reportService.updateReportSlaData(slaDataInput);
                        // 触发中止方法
                        slaPublish.stop(scheduleStopRequest);
                        log.warn("【SLA】成功发送压测任务终止事件，并记录sla熔断数据");
                    }
                } catch (Exception e) {
                    log.warn("【SLA】发送压测任务终止事件失败:{}", e.getMessage(), e);
                }
            } else {
                redisClientUtils.hmset(SLA_DESTORY_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private void doWarn(SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
                        SendMetricsEvent metricsEvent, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            SceneSlaRefInput input = new SceneSlaRefInput();
            BeanUtils.copyProperties(dto,input);
            Map<String, Object> conditionMap = SlaUtil.matchCondition(input, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                redisClientUtils.hmdelete(SLA_WARN_KEY, String.valueOf(dto.getId()));
                return;
            }

            String object = (String)redisClientUtils.hmget(SLA_WARN_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                redisClientUtils.hmset(SLA_WARN_KEY, dataMap, EXPIRE_TIME);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                //报告未结束，才insert
                if (redisClientUtils.hasKey(PREFIX_TASK + metricsEvent.getSceneId())) {
                    TWarnDetailMapper.insertSelective(warnDetail);
                }
            } else {
                redisClientUtils.hmset(SLA_WARN_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private Boolean matchContinue(AchieveModel model, Long timestamp) {
        if (model == null) {
            return false;
        }
        log.info("【sla】校验是否连续，上次触发时间={}, 当前时间={}，相差={}", model.getLastAchieveTime(), timestamp,
            (timestamp - model.getLastAchieveTime()));
        //        if(timestamp - model.getLastAchieveTime() <= 6000) {
        //            return true;
        //        }
        return true;
    }

    private WarnDetail buildWarnDetail(Map<String, Object> conditionMap,
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent,
        SceneManageWrapperOutput.SceneSlaRefOutput salDTO) {
        WarnDetail warnDetail = new WarnDetail();
        warnDetail.setPtId(metricsEvent.getReportId());
        warnDetail.setSlaId(salDTO.getId());
        warnDetail.setSlaName(salDTO.getRuleName());
        warnDetail.setBusinessActivityId(businessActivityDTO.getBusinessActivityId());
        warnDetail.setBusinessActivityName(businessActivityDTO.getBusinessActivityName());
        StringBuffer sb = new StringBuffer();
        sb.append(conditionMap.get("type"));
        sb.append(conditionMap.get("compare"));
        sb.append(salDTO.getRule().getDuring());
        sb.append(conditionMap.get("unit"));
        sb.append(", 连续");
        sb.append(salDTO.getRule().getTimes());
        sb.append("次");
        warnDetail.setWarnContent(sb.toString());
        warnDetail.setWarnTime(DateUtil.getDate(DateUtil.formatTime(metricsEvent.getTimestamp())));
        warnDetail.setRealValue((Double)conditionMap.get("real"));
        return warnDetail;
    }

    private List<SceneManageWrapperOutput.SceneSlaRefOutput> filterSlaList(Long businessActivityId, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return Collections.EMPTY_LIST;
        }
        return slaList.stream().filter(data -> checkContain(data.getBusinessActivity(), businessActivityId))
            .collect(Collectors.toList());
    }

    private Boolean checkContain(String[] businessActivity, Long businessActivityId) {
        if (businessActivity == null || businessActivity.length == 0) {
            return false;
        }
        for (String data : businessActivity) {
            if ("-1".equals(data) || String.valueOf(businessActivityId).equals(data)) {
                return true;
            }
        }
        return false;
    }

    private SceneManageWrapperOutput getSceneManageWrapperDTO(Long sceneId) {
        String object = (String)redisClientUtils.hmget(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (object != null) {
            return JSON.parseObject(object, SceneManageWrapperOutput.class);
        }
        ;
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeSLA(true);
        SceneManageWrapperOutput dto = sceneManageService.getSceneManage(sceneId, options);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put(String.valueOf(sceneId), JSON.toJSONString(dto));
        redisClientUtils.hmset(SLA_SCENE_KEY, dataMap, EXPIRE_TIME);
        return dto;
    }
}
