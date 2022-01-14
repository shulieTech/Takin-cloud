package io.shulie.takin.cloud.biz.service.async.impl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageStartRecordVO;

import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.cloud.ext.api.EngineCallExtApi;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.biz.service.async.AsyncService;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.collector.collector.CollectorService;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;

/**
 * @author qianshui
 * @date 2020/10/30 下午7:13
 */
@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Autowired
    private EventCenterTemplate eventCenterTemplate;

    @Autowired
    private SceneManageService sceneManageService;

    @Resource
    private SceneManageDAO sceneManageDAO;

    /**
     * 压力节点 启动时间超时
     */
    @Value("${pressure.node.start.expireTime: 30}")
    private Integer pressureNodeStartExpireTime;

    @Autowired
    private EnginePluginUtils enginePluginUtils;

    /**
     * 线程定时检查休眠时间
     */
    private final static Integer CHECK_INTERVAL_TIME = 3;

    @Async("checkStartedPodPool")
    @Override
    public void checkStartedTask(ScheduleStartRequestExt startRequest) {
        log.info("启动后台检查压测任务状态线程.....");
        int currentTime = 0;
        boolean checkPass = false;

        String pressureNodeTotalName = ScheduleConstants.getPressureNodeTotalKey(startRequest.getSceneId(), startRequest.getTaskId(), startRequest.getTenantId());
        String pressureNodeName = ScheduleConstants.getPressureNodeName(startRequest.getSceneId(), startRequest.getTaskId(), startRequest.getTenantId());
        String pressureNodeTotal = redisClientUtils.getString(pressureNodeTotalName);
        while (currentTime <= pressureNodeStartExpireTime) {
            String pressureNodeNum = redisClientUtils.getString(pressureNodeName);
            log.info("任务id={}, 计划启动【{}】个节点，当前启动【{}】个节点.....", startRequest.getTaskId(), pressureNodeTotal, pressureNodeNum);
            if (pressureNodeTotal != null && pressureNodeNum != null) {
                try {
                    if (Integer.parseInt(pressureNodeNum) == Integer.parseInt(pressureNodeTotal)) {
                        checkPass = true;
                        log.info("后台检查到pod全部启动成功.....");
                        break;
                    }
                } catch (Exception e) {
                    log.error("异常代码【{}】,异常内容：任务启动异常 --> 从Redis里获取节点数量数据格式异常: {}",
                        TakinCloudExceptionEnum.TASK_START_ERROR_CHECK_POD, e);
                }
            }
            try {
                Thread.sleep(CHECK_INTERVAL_TIME * 1000);
            } catch (InterruptedException e) {
                log.warn("进程暂停异常", e);
            }
            currentTime += CHECK_INTERVAL_TIME;
        }
        //压力节点 没有在设定时间内启动完毕，停止压测
        if (!checkPass) {
            log.info("调度任务{}-{}-{},压力节点 没有在设定时间{}s内启动，停止压测,", startRequest.getSceneId(), startRequest.getTaskId(),
                startRequest.getTenantId(), CHECK_INTERVAL_TIME);

            if (pressureNodeTotal != null) {
                int podTotalNum = Integer.parseInt(pressureNodeTotal);
                for (int i = 1; i <= podTotalNum; i++) {
                    String enginePodNoStartKey = ScheduleConstants.getEnginePodNoStartKey(startRequest.getSceneId(), startRequest.getTaskId(),
                        startRequest.getTenantId(), i + "", CollectorService.METRICS_EVENTS_STARTED);
                    if (!redisClientUtils.hasKey(enginePodNoStartKey)) {
                        log.warn("调度任务 pod " + i + "没有在设定时间启动，redisKey为" + enginePodNoStartKey);
                    }
                }
            }
            // 记录停止原因
            // 补充停止原因
            //设置缓存，用以检查压测场景启动状态 lxr 20210623
            String k8sPodKey = String.format(SceneTaskRedisConstants.PRESSURE_NODE_ERROR_KEY + "%s_%s", startRequest.getSceneId(), startRequest.getTaskId());
            String startedPodNum = redisClientUtils.getString(pressureNodeName);
            redisClientUtils.hmset(k8sPodKey, SceneTaskRedisConstants.PRESSURE_NODE_START_ERROR,
                String.format("节点没有在设定时间【%s】s内启动，计划启动节点个数【%s】,实际启动节点个数【%s】,"
                    + "导致压测停止", pressureNodeStartExpireTime, redisClientUtils.getString(pressureNodeTotalName),
                    StringUtils.isBlank(startedPodNum)? 0: startedPodNum));
            callStop(startRequest);
        }
    }

    @Async("updateStatusPool")
    @Override
    public void updateSceneRunningStatus(Long sceneId, Long reportId, Long customerId) {
        while (true) {
            boolean isSceneFinished = isSceneFinished(reportId);
            boolean jobFinished = isJobFinished(sceneId, reportId, customerId);
            if (jobFinished || isSceneFinished) {
                String statusKey = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId,
                    reportId);
                boolean updateResult = redisClientUtils.hmset(statusKey, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY,
                    SceneRunTaskStatusEnum.ENDED.getText());
                if (updateResult) {
                    log.info("更新场景运行状态缓存成功。场景ID:{},报告ID:{}", sceneId, reportId);
                } else {
                    log.error("更新场景运行状态缓存失败。场景ID:{},报告ID:{}", sceneId, reportId);
                }
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_TIME);
            } catch (InterruptedException e) {
                log.error("更新场景运行状态缓存失败！异常信息:{}", e.getMessage());
            }
        }
    }

    private boolean isSceneFinished(Long sceneId) {
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (Objects.isNull(sceneManage) || Objects.isNull(sceneManage.getStatus())) {
            return true;
        }
        return SceneManageStatusEnum.ifFinished(sceneManage.getStatus());
    }

    private boolean isJobFinished(Long sceneId, Long reportId, Long customerId) {
        String jobName = ScheduleConstants.getScheduleName(sceneId, reportId, customerId);
        EngineCallExtApi engineCallExtApi = enginePluginUtils.getEngineCallExtApi();
        return !SceneManageConstant.SCENE_TASK_JOB_STATUS_RUNNING.equals(engineCallExtApi.getJobStatus(jobName));
    }

    private void callStop(ScheduleStartRequestExt startRequest) {
        // 汇报失败
        sceneManageService.reportRecord(SceneManageStartRecordVO.build(startRequest.getSceneId(),
            startRequest.getTaskId(),
            startRequest.getTenantId()).success(false).errorMsg("").build());
        // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告拦截 状态拦截
        Event event = new Event();
        event.setEventName("finished");
        event.setExt(new TaskResult(startRequest.getSceneId(), startRequest.getTaskId(), startRequest.getTenantId()));
        eventCenterTemplate.doEvents(event);
    }
}
