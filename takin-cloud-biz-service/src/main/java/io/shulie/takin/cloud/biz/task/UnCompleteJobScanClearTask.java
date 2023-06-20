package io.shulie.takin.cloud.biz.task;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.service.RedissonDistributedLock;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.param.scenemanage.SceneManageCreateOrUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageRunningResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xjz@io.shulie
 * @date 2023/6/16
 * @desc 未完成job扫描  
 */
@Component
@Slf4j
public class UnCompleteJobScanClearTask  extends AbstractIndicators {
    
    @Resource
    private SceneManageDAO sceneManageDAO;

    @Resource
    private SceneManageService sceneManageService;
    
    @Resource
    private SceneTaskService sceneTaskService;
    
    @Resource
    private ReportDao reportDao;
    
    
    @Value("${uncompleted.clear.job.delay.time:5}")
    private Integer delayTime;
    
    @Value("${uncompleted.need.report:false}")
    private Boolean needReport;
    
    @Resource
    private RedissonDistributedLock redissonDistributedLock;
    
    @Scheduled(fixedDelay = 5,timeUnit = TimeUnit.MINUTES)
    public void scanSceneManages() {
      
        String key = "un:complete:job:scan:clear:task";
        
        try {
            if(!redissonDistributedLock.tryLock(key,1L,60L,TimeUnit.SECONDS)){
                return;
            }
            log.info("扫描超时未完压测场景任务执行中...");
            scanUnCompletedSceneForceStop();
        }catch (Exception e){
            log.error("获取redission分布式锁异常",e);
        }finally {
            redissonDistributedLock.unLock(key);
        }
    }
    
    
    public void scanUnCompletedSceneForceStop(){
        List<SceneManageRunningResp> list = null;
        try {
            int page = 1;
            // 基本200条够了，不够在使用循环
            int size = 200;
            list = sceneManageService.getSceneManageRunningList(page,size);
            if(CollectionUtils.isEmpty(list)){
                return;
            }
            stop(list);
            
        }catch (Exception e){
            log.error("获取压测中场景列表异常",e);
        }
        
      
       
    }
    
    private void stop(List<SceneManageRunningResp> list){
        for (SceneManageRunningResp runningResp : list) {
            if (runningResp.getDuration() < 1 || runningResp.getLastPtTime() == null) {
                continue;
            }
            ReportResult reportBySceneId = reportDao.getRecentlyReport(runningResp.getId());
            if (reportBySceneId == null){
                forceStopSceneManage(runningResp.getId());
                log.info("停止压测场景id={}成功,未查询到报告直接结束", runningResp.getId());
                continue;
            }
            // 压测开始时间
            long startTime = reportBySceneId.getStartTime().getTime();
            // 施压时间
            long pressureTime = runningResp.getDuration() * 60*1000;
            // 延迟计算时间
            long delay = delayTime * 60 * 1000;
            // 强制结束时间
            long endTime =  startTime + pressureTime + delay;
            long now = System.currentTimeMillis();
            log.info("压测应结束时间:{},当前时间:{}",endTime,now);
            // 当前时间大于就直接强制结束
            if(now > endTime){
                // 直接结束场景
                log.info("正在停止id={}的压测场景",runningResp.getId());
                try {
                    forceStopSceneManage(runningResp.getId());
                    // 强制结束
                    sceneTaskService.forceStopTask(reportBySceneId.getId(), needReport);
                }catch (Exception e){
                    log.error("停止压测场景id={},异常",runningResp.getId(),e);
                }
            }
        }
    }

    /**
     * 更改压测场景
     * @param id 场景id
     */
    private void forceStopSceneManage(Long id){
        SceneManageCreateOrUpdateParam updateParam = new SceneManageCreateOrUpdateParam();
        updateParam.setLastPtTime(new Date());
        updateParam.setId(id);
        updateParam.setUpdateTime(new Date());
        updateParam.setStatus(SceneManageStatusEnum.WAIT.getValue());
        sceneManageDAO.update(updateParam);
    }
   
}
