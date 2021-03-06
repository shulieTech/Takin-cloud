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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;

import com.google.common.collect.Maps;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

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
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.data.result.scenemanage.SceneSlaRefResult;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageWrapperResult;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;

/**
 * @author qianshui
 * @date 2020/4/20 ??????4:48
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
    private TWarnDetailMapper tWarnDetailMapper;
    @Resource
    private SceneManageService sceneManageService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
                log.warn("??????sla??????,?????????????????????.{}", JSON.toJSONString(metrics));
                return false;
            }
            if (Objects.isNull(dto.getPressureType()) || dto.getPressureType().equals(PressureSceneEnum.TRY_RUN.getCode())) {
                log.info("?????????????????????SLA??????");
                return false;
            }
        } catch (Exception e) {
            log.error("??????sla??????,???????????????????????????.{}", JSON.toJSONString(metrics), e);
            return false;
        }
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivity =
            dto.getBusinessActivityConfig()
                .stream()
                .filter(data -> metrics.getTransaction().equals(data.getBindRef()))
                .findFirst()
                .orElse(null);

        if (businessActivity == null) {
            log.warn("??????sla??????,?????????????????????.{}", JSON.toJSONString(metrics));
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
        String scene = (String)stringRedisTemplate.opsForHash().get(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (scene == null) {
            return;
        }
        SceneManageWrapperResult dto = JSON.parseObject(scene, SceneManageWrapperResult.class);

        dto.getStopCondition().stream().map(SceneSlaRefResult::getId).forEach(
            id -> stringRedisTemplate.opsForHash().delete(SLA_DESTROY_KEY, String.valueOf(id)));
        dto.getWarningCondition().stream().map(SceneSlaRefResult::getId).forEach(
            id -> stringRedisTemplate.opsForHash().delete(SLA_WARN_KEY, String.valueOf(id)));
        stringRedisTemplate.opsForHash().delete(SLA_SCENE_KEY, String.valueOf(sceneId));
        stringRedisTemplate.delete(PREFIX_TASK + sceneId);
        log.info("??????SLA????????????????????????, sceneId={}", sceneId);
    }

    @Override
    public void cacheData(Long sceneId) {
        stringRedisTemplate.opsForValue().set(PREFIX_TASK + sceneId, "on", 7, TimeUnit.DAYS);
    }

    private void doDestroy(Long sceneId, SendMetricsEvent metricsEvent,
        List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList,
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            SceneSlaRefInput input = BeanUtil.copyProperties(dto, SceneSlaRefInput.class);
            Map<String, Object> conditionMap = SlaUtil.matchCondition(input, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                stringRedisTemplate.opsForHash().delete(SLA_DESTROY_KEY, String.valueOf(dto.getId()));
                return;
            }
            String object = (String)stringRedisTemplate.opsForHash().get(SLA_DESTROY_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                stringRedisTemplate.opsForHash().putAll(SLA_DESTROY_KEY, dataMap);
                stringRedisTemplate.expire(SLA_DESTROY_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                try {
                    ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
                    scheduleStopRequest.setTaskId(metricsEvent.getReportId());
                    scheduleStopRequest.setSceneId(sceneId);
                    // ????????????id
                    scheduleStopRequest.setTenantId(metricsEvent.getTenantId());
                    Map<String, Object> extendMap = new HashMap<>(1);
                    extendMap.put(Constants.SLA_DESTROY_EXTEND, "SLA??????????????????????????????");
                    scheduleStopRequest.setExtend(extendMap);
                    //???????????????????????????
                    if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX_TASK + metricsEvent.getSceneId()))) {
                        // ???????????????????????????????????????
                        WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                        tWarnDetailMapper.insertSelective(warnDetail);
                        // ??????sla????????????
                        UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
                        SlaBean slaBean = new SlaBean();
                        slaBean.setRuleName(dto.getRuleName());
                        slaBean.setBusinessActivity(businessActivityDTO.getBusinessActivityName());
                        slaBean.setBindRef(businessActivityDTO.getBindRef());
                        slaBean.setRule(warnDetail.getWarnContent());
                        slaDataInput.setReportId(scheduleStopRequest.getTaskId());
                        slaDataInput.setSlaBean(slaBean);
                        reportService.updateReportSlaData(slaDataInput);
                        // ??????????????????
                        slaPublish.stop(scheduleStopRequest);
                        log.warn("???SLA???????????????????????????????????????????????????sla????????????");
                    }
                } catch (Exception e) {
                    log.warn("???SLA???????????????????????????????????????:{}", e.getMessage(), e);
                }
            } else {
                stringRedisTemplate.opsForHash().put(SLA_DESTROY_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private void doWarn(SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            SceneSlaRefInput input = BeanUtil.copyProperties(dto, SceneSlaRefInput.class);
            Map<String, Object> conditionMap = SlaUtil.matchCondition(input, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                stringRedisTemplate.opsForHash().delete(SLA_WARN_KEY, String.valueOf(dto.getId()));
                return;
            }

            String object = (String)stringRedisTemplate.opsForHash().get(SLA_WARN_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                stringRedisTemplate.opsForHash().putAll(SLA_WARN_KEY, dataMap);
                stringRedisTemplate.expire(SLA_WARN_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                //?????????????????????insert
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX_TASK + metricsEvent.getSceneId()))) {
                    tWarnDetailMapper.insertSelective(warnDetail);
                }
            } else {
                stringRedisTemplate.opsForHash().put(SLA_WARN_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private Boolean matchContinue(AchieveModel model, Long timestamp) {
        if (model == null) {
            return false;
        }
        log.info("???sla??????????????????????????????????????????={}, ????????????={}?????????={}",
            model.getLastAchieveTime(), timestamp,
            (timestamp - model.getLastAchieveTime()));
        return true;
    }

    /**
     * ??????????????????
     *
     * @param conditionMap        ??????Map
     * @param businessActivityDTO ???????????????????????????????????????
     * @param metricsEvent        ??????
     * @param slaDto              sla??????
     * @return ????????????
     */
    private WarnDetail buildWarnDetail(Map<String, Object> conditionMap,
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent,
        SceneManageWrapperOutput.SceneSlaRefOutput slaDto) {
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
            + ", ??????"
            + slaDto.getRule().getTimes()
            + "???";
        warnDetail.setWarnContent(sb);
        warnDetail.setWarnTime(DateUtil.date(metricsEvent.getTimestamp()));
        warnDetail.setRealValue((Double)conditionMap.get("real"));
        return warnDetail;
    }

    private List<SceneManageWrapperOutput.SceneSlaRefOutput> filterSlaListByMd5(String bindRef,
        List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
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

    private List<SceneManageWrapperOutput.SceneSlaRefOutput> filterSlaListByActivityId(Long businessActivityId,
        List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
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
     * ???????????????????????????????????????????????????????????????????????????????????????"??????"?????????sla??????
     *
     * @param sceneId  ??????ID
     * @param reportId ??????ID
     * @return ????????????
     */
    private SceneManageWrapperOutput getSceneManageWrapperDTO(Long sceneId, Long reportId) {
        String object = (String)stringRedisTemplate.opsForHash().get(SLA_SCENE_KEY, String.valueOf(sceneId));
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
        Map<String, String> dataMap = new HashMap<>(1);
        dataMap.put(String.valueOf(sceneId), JSON.toJSONString(dto));
        stringRedisTemplate.opsForHash().putAll(SLA_SCENE_KEY, dataMap);
        stringRedisTemplate.expire(SLA_SCENE_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
        return dto;
    }
}
