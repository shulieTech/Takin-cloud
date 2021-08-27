package io.shulie.takin.cloud.biz.service.async.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.pamirs.takin.entity.dao.report.TReportMapper;
import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.biz.service.async.AsyncService;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.ReportConstans;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @ClassName AsyncServiceImpl
 * @Description
 * @Author qianshui
 * @Date 2020/10/30 下午7:13
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
    private TReportMapper tReportMapper;

    /**
     * 压力节点 启动时间超时
     */
    @Value("${pressure.node.start.expireTime: 30}")
    private Integer pressureNodeStartExpireTime;

    /**
     * 线程定时检查休眠时间
     */
    private final static Integer CHECK_INTERVAL_TIME = 3;

    @Async("checkStartedPodPool")
    @Override
    public void checkStartedTask(ScheduleStartRequestExt startRequest) {
        log.info("启动后台检查压测任务状态线程.....");
        Integer currentTime = 0;
        boolean checkPass = false;

        String pressureNodeTotalName = ScheduleConstants.getPressureNodeTotalKey(startRequest.getSceneId(), startRequest.getTaskId(), startRequest.getCustomerId());
        String pressureNodeName = ScheduleConstants.getPressureNodeName(startRequest.getSceneId(), startRequest.getTaskId(), startRequest.getCustomerId());
        while (currentTime <= pressureNodeStartExpireTime) {
            String pressureNodeTotal = redisClientUtils.getString(pressureNodeTotalName);
            String pressureNodeNum = redisClientUtils.getString(pressureNodeName);
            log.info("任务id={}, 计划启动【{}】个节点，当前启动【{}】个节点.....", startRequest.getTaskId(), pressureNodeTotal, pressureNodeNum);
            if(pressureNodeTotal != null && pressureNodeNum != null) {
                try {
                    if (Integer.parseInt(pressureNodeNum) == Integer.parseInt(pressureNodeTotal)) {
                        checkPass = true;
                        log.info("后台检查到pod全部启动成功.....");
                        break;
                    }
                } catch (Exception e) {
                    log.error("异常代码【{}】,异常内容：任务启动异常 --> 从Redis里获取节点数量数据格式异常: {}",
                            TakinCloudExceptionEnum.TASK_START_ERROR_CHECK_POD,e);
                }
            }
            try {
                Thread.sleep(CHECK_INTERVAL_TIME * 1000);
            } catch (InterruptedException e) {
                log.warn("进程暂停异常",e);
            }
            currentTime += CHECK_INTERVAL_TIME;
        }
        //压力节点 没有在设定时间内启动完毕，停止压测
        if(!checkPass) {
            log.info("调度任务{}-{}-{},压力节点 没有在设定时间{}s内启动，停止压测,",startRequest.getSceneId(), startRequest.getTaskId(),
                startRequest.getCustomerId(),CHECK_INTERVAL_TIME);
            // 记录停止原因
            // 补充停止原因
            //设置缓存，用以检查压测场景启动状态 lxr 20210623
            String k8sPodKey = String.format(SceneTaskRedisConstants.PRESSURE_NODE_ERROR_KEY + "%s_%s", startRequest.getSceneId(), startRequest.getTaskId());
            redisClientUtils.hmset(k8sPodKey, SceneTaskRedisConstants.PRESSURE_NODE_START_ERROR,
                String.format("节点没有在设定时间【%s】s内启动，计划启动节点个数【%s】,实际启动节点个数【%s】,"
                    + "，导致压测停止", pressureNodeStartExpireTime, redisClientUtils.getString(pressureNodeTotalName),redisClientUtils.getString(pressureNodeName)));
            callStop(startRequest);
        }
    }

    @Async("updateStatusPool")
    @Override
    public void updateSceneRunningStatus(Long sceneId, Long reportId) {
        while (true){
            boolean reportFinished = isReportFinished(reportId);
            if (reportFinished){
                String statusKey = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId,
                    reportId);
                boolean updateResult = redisClientUtils.hmset(statusKey, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY,
                    SceneRunTaskStatusEnum.ENDED.getText());
                if (updateResult){
                    log.info("更新场景运行状态缓存成功，报告已完成。场景ID:{},报告ID:{}",sceneId,reportId);
                }else {
                    log.error("更新场景运行状态缓存失败，报告已完成。场景ID:{},报告ID:{}",sceneId,reportId);
                }
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_TIME);
            } catch (InterruptedException e) {
                log.error("更新场景运行状态缓存失败！异常信息:{}",e.getMessage());
            }
        }
    }

    private boolean isReportFinished(Long reportId) {
        Report report = tReportMapper.selectByPrimaryKey(reportId);
        return report.getStatus() == ReportConstans.FINISH_STATUS;
    }

    private void callStop(ScheduleStartRequestExt startRequest) {
        // 汇报失败
        sceneManageService.reportRecord(SceneManageStartRecordVO.build(startRequest.getSceneId(),
            startRequest.getTaskId(),
            startRequest.getCustomerId()).success(false).errorMsg("").build());
        // 清除 SLA配置 清除PushWindowDataScheduled 删除pod job configMap  生成报告拦截 状态拦截
        Event event = new Event();
        event.setEventName("finished");
        event.setExt(new TaskResult(startRequest.getSceneId(), startRequest.getTaskId(), startRequest.getCustomerId()));
        eventCenterTemplate.doEvents(event);
    }
}
