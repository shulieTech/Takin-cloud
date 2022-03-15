package io.shulie.takin.cloud.biz.service.sla.impl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.pamirs.takin.entity.dao.scene.manage.TWarnDetailMapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;

import io.shulie.takin.cloud.biz.utils.SlaUtil;
import io.shulie.takin.cloud.biz.event.SlaPublish;
import io.shulie.takin.cloud.sdk.model.common.SlaBean;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.biz.service.sla.SlaService;
import io.shulie.takin.cloud.common.constants.Constants;
import io.shulie.takin.cloud.common.bean.sla.AchieveModel;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageWrapperResult;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse.SceneSlaRefResponse;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;

/**
 * @author qianshui
 * @date 2020/4/20 下午4:48
 */
@Service
@Slf4j
public class SlaServiceImpl implements SlaService {

    public static final Long EXPIRE_TIME = 24 * 3600L;
    public static final String PREFIX_TASK = "TAKIN:SLA:TASK:";
    public static final String SLA_WARN_KEY = "TAKIN:SLA:WARN:KEY";
    public static final String SLA_SCENE_KEY = "TAKIN:SLA:SCENE:KEY";
    public static final String SLA_DESTROY_KEY = "TAKIN:SLA:DESTROY:KEY";

    @Resource
    private ReportDao reportDao;
    @Resource
    private SlaPublish slaPublish;
    @Resource
    private ReportService reportService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private TWarnDetailMapper tWarnDetailMapper;
    @Resource
    private SceneManageService sceneManageService;

    @Override
    public Boolean buildWarn(SendMetricsEvent metrics) {
        if (StringUtils.isBlank(metrics.getTransaction())
            || "all".equalsIgnoreCase(metrics.getTransaction())) {
            return true;
        }
        Long sceneId = metrics.getSceneId();
        SceneManageWrapperOutput dto;
        try {
            dto = getSceneManageWrapperDTO(sceneId, metrics.getReportId());
            if (dto == null) {
                log.warn("构建sla异常,未找到压测场景.{}", JSON.toJSONString(metrics));
                return false;
            }
            if (Objects.isNull(dto.getPressureType()) || dto.getPressureType().equals(PressureSceneEnum.TRY_RUN.getCode())) {
                log.info("脚本调试不进行SLA校验");
                return false;
            }
        } catch (Exception e) {
            log.error("构建sla异常,查找到压测场景异常.{}", JSON.toJSONString(metrics), e);
            return false;
        }
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivity =
            dto.getBusinessActivityConfig()
                .stream()
                .filter(data -> metrics.getTransaction().equals(data.getBindRef()))
                .findFirst()
                .orElse(null);

        if (businessActivity == null) {
            log.warn("构建sla异常,未找到业务活动.{}", JSON.toJSONString(metrics));
            return false;
        }
        if (StringUtils.isBlank(dto.getScriptAnalysisResult())) {
            Long businessActivityId = businessActivity.getBusinessActivityId();

            doDestroy(dto.getId(), metrics, filterSlaListByActivityId(businessActivityId, dto.getStopCondition()), businessActivity);

            doWarn(businessActivity, metrics, filterSlaListByActivityId(businessActivityId, dto.getWarningCondition()));
        } else {
            String bindRef = businessActivity.getBindRef();
            doDestroy(dto.getId(), metrics, filterSlaListByMd5(bindRef, dto.getStopCondition()), businessActivity);

            doWarn(businessActivity, metrics, filterSlaListByMd5(bindRef, dto.getWarningCondition()));
        }
        return true;
    }

    @Override
    public void removeMap(Long sceneId) {
        String scene = (String)redisTemplate.opsForHash().get(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (scene == null) {
            return;
        }
        SceneManageWrapperResult dto = JSON.parseObject(scene, SceneManageWrapperResult.class);

        dto.getStopCondition().stream().map(SceneSlaRefResponse::getId).forEach(
            id -> redisTemplate.opsForHash().delete(SLA_DESTROY_KEY, String.valueOf(id)));
        dto.getWarningCondition().stream().map(SceneSlaRefResponse::getId).forEach(
            id -> redisTemplate.opsForHash().delete(SLA_WARN_KEY, String.valueOf(id)));
        redisTemplate.opsForHash().delete(SLA_SCENE_KEY, String.valueOf(sceneId));
        redisTemplate.delete(PREFIX_TASK + sceneId);
        log.info("清除SLA分析内存配置成功, sceneId={}", sceneId);
    }

    @Override
    public void cacheData(Long sceneId) {
        redisTemplate.opsForValue().set(PREFIX_TASK + sceneId, "on", 7, TimeUnit.DAYS);
    }

    private void doDestroy(Long sceneId, SendMetricsEvent metricsEvent,
        List<SceneSlaRefResponse> slaList,
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            Map<String, Object> conditionMap = SlaUtil.matchCondition(dto, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                redisTemplate.opsForHash().delete(SLA_DESTROY_KEY, String.valueOf(dto.getId()));
                return;
            }
            String object = (String)redisTemplate.opsForHash().get(SLA_DESTROY_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                redisTemplate.opsForHash().putAll(SLA_DESTROY_KEY, dataMap);
                redisTemplate.expire(SLA_DESTROY_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
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
                    scheduleStopRequest.setTenantId(metricsEvent.getTenantId());
                    Map<String, Object> extendMap = new HashMap<>(1);
                    extendMap.put(Constants.SLA_DESTROY_EXTEND, "SLA发送压测任务终止事件");
                    scheduleStopRequest.setExtend(extendMap);
                    //报告未结束，才通知
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_TASK + metricsEvent.getSceneId()))) {
                        // 熔断数据也记录到告警明细中
                        WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                        tWarnDetailMapper.insertSelective(warnDetail);
                        // 记录sla熔断数据
                        UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
                        SlaBean slaBean = new SlaBean();
                        slaBean.setRuleName(dto.getRuleName());
                        slaBean.setBusinessActivity(businessActivityDTO.getBusinessActivityName());
                        slaBean.setBindRef(businessActivityDTO.getBindRef());
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
                redisTemplate.opsForHash().put(SLA_DESTROY_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private void doWarn(SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent, List<SceneSlaRefResponse> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            Map<String, Object> conditionMap = SlaUtil.matchCondition(dto, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                redisTemplate.opsForHash().delete(SLA_WARN_KEY, String.valueOf(dto.getId()));
                return;
            }

            String object = (String)redisTemplate.opsForHash().get(SLA_WARN_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                redisTemplate.opsForHash().putAll(SLA_WARN_KEY, dataMap);
                redisTemplate.expire(SLA_WARN_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                //报告未结束，才insert
                if (Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_TASK + metricsEvent.getSceneId()))) {
                    tWarnDetailMapper.insertSelective(warnDetail);
                }
            } else {
                redisTemplate.opsForHash().put(SLA_WARN_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private Boolean matchContinue(AchieveModel model, Long timestamp) {
        if (model == null) {
            return false;
        }
        log.info("【sla】校验是否连续，上次触发时间={}, 当前时间={}，相差={}",
            model.getLastAchieveTime(), timestamp,
            (timestamp - model.getLastAchieveTime()));
        return true;
    }

    /**
     * 创建告警明细
     *
     * @param conditionMap        条件Map
     * @param businessActivityDTO 关联的业务活动（脚本节点）
     * @param metricsEvent        数据
     * @param slaDto              sla内容
     * @return 告警条件
     */
    private WarnDetail buildWarnDetail(Map<String, Object> conditionMap,
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent,
        SceneSlaRefResponse slaDto) {
        WarnDetail warnDetail = new WarnDetail();
        warnDetail.setPtId(metricsEvent.getReportId());
        warnDetail.setSlaId(slaDto.getId());
        warnDetail.setSlaName(slaDto.getRuleName());
        warnDetail.setBindRef(businessActivityDTO.getBindRef());
        warnDetail.setBusinessActivityId(businessActivityDTO.getBusinessActivityId());
        warnDetail.setBusinessActivityName(businessActivityDTO.getBusinessActivityName());
        String sb = String.valueOf(conditionMap.get("type"))
            + conditionMap.get("compare")
            + slaDto.getRule().getDuring()
            + conditionMap.get("unit")
            + ", 连续"
            + slaDto.getRule().getTimes()
            + "次";
        warnDetail.setWarnContent(sb);
        warnDetail.setWarnTime(DateUtil.date(metricsEvent.getTimestamp()));
        warnDetail.setRealValue((Double)conditionMap.get("real"));
        return warnDetail;
    }

    private List<SceneSlaRefResponse> filterSlaListByMd5(String bindRef,
        List<SceneSlaRefResponse> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return new ArrayList<>(0);
        }
        return slaList.stream().filter(data -> checkContainByMd5(data.getBusinessActivity(), bindRef))
            .collect(Collectors.toList());
    }

    private Boolean checkContainByMd5(String[] md5s, String bindRef) {
        if (md5s == null || md5s.length == 0) {
            return false;
        }
        for (String data : md5s) {
            if ("-1".equals(data)
                || ReportConstants.TEST_PLAN_MD5.equals(data)
                || ReportConstants.ALL_BUSINESS_ACTIVITY.equals(data)
                || String.valueOf(bindRef).equals(data)) {
                return true;
            }
        }
        return false;
    }

    private List<SceneSlaRefResponse> filterSlaListByActivityId(Long businessActivityId,
        List<SceneSlaRefResponse> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return new ArrayList<>(0);
        }
        return slaList.stream().filter(data -> checkContainByActivityId(data.getBusinessActivity(), businessActivityId))
            .collect(Collectors.toList());
    }

    private Boolean checkContainByActivityId(String[] businessActivity, Long businessActivityId) {
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

    /**
     * 获取场景信息，并将脚本中测试计划填充到场景关联的节点中作为"全部"来进行sla计算
     *
     * @param sceneId  场景ID
     * @param reportId 报告ID
     * @return 场景信息
     */
    private SceneManageWrapperOutput getSceneManageWrapperDTO(Long sceneId, Long reportId) {
        String object = (String)redisTemplate.opsForHash().get(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (object != null) {
            return JSON.parseObject(object, SceneManageWrapperOutput.class);
        }
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeSLA(true);
        SceneManageWrapperOutput dto = sceneManageService.getSceneManage(sceneId, options);
        List<ReportBusinessActivityDetailEntity> testPlan = reportDao
            .getReportBusinessActivityDetailsByReportId(reportId, NodeTypeEnum.TEST_PLAN);
        if (CollectionUtils.isNotEmpty(testPlan) && testPlan.size() == 1) {
            ReportBusinessActivityDetailEntity detailEntity = testPlan.get(0);
            SceneBusinessActivityRefOutput refOutput = new SceneBusinessActivityRefOutput();
            refOutput.setApplicationIds(detailEntity.getApplicationIds());
            refOutput.setBindRef(detailEntity.getBindRef());
            refOutput.setBusinessActivityId(detailEntity.getBusinessActivityId());
            refOutput.setBusinessActivityName(detailEntity.getBusinessActivityName());
            refOutput.setTargetRT(detailEntity.getTargetRt().intValue());
            refOutput.setTargetSA(detailEntity.getTargetSa());
            refOutput.setTargetSuccessRate(detailEntity.getTargetSuccessRate());
            refOutput.setTargetTPS(detailEntity.getTargetTps().intValue());
            dto.getBusinessActivityConfig().add(refOutput);
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put(String.valueOf(sceneId), JSON.toJSONString(dto));
        redisTemplate.opsForHash().putAll(SLA_SCENE_KEY, dataMap);
        redisTemplate.expire(SLA_SCENE_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
        return dto;
    }
}
