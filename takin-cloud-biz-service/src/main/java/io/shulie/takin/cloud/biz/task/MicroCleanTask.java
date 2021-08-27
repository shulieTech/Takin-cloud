package io.shulie.takin.cloud.biz.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageListResult;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.ext.content.enginecall.ScheduleStopRequestExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 * 清理未正常关闭的远程调用任务
 */
@Component
@Slf4j
public class MicroCleanTask implements InitializingBean {


    @Value("${clear.task.scene.status:0-6-8-9-10}")
    private String clearTaskSceneStatus;
    @Value("${clean.task.scheduling.enabled:true}")
    private Boolean schedulingEnabled;

    private final List<Integer> clearSceneStatus = new ArrayList<>();
    /**
     * 不处理的taskName
     */
    private final List<String> noDealTaskNames = new ArrayList<>();

    @Autowired
    private EnginePluginUtils pluginUtils;

    @Autowired
    private SceneManageDAO sceneManageDAO;


    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(2, 6,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(256), new ThreadFactoryBuilder()
            .setNameFormat("micro-clean-task-%d").build(), new ThreadPoolExecutor.AbortPolicy());


    @Scheduled(cron = "${clear.task.corn:*/20 * * * * ?}")
    public void clearTask(){
        if(!schedulingEnabled) {
            return;
        }
        //获取所有job
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        if (engineCallExtApi == null){
            log.warn("兜底删除任务中没有获取到插件，如果不需要兜底任务，请设置clean.task.scheduling.enabled为false！");
            return;
        }
        List<String> jobNames =  engineCallExtApi.getAllRunningJobName();
        if (CollectionUtils.isNotEmpty(jobNames)){
            //获取其中属于我们的压测任务
            List<String> sceneTaskJobNames = jobNames.stream().filter(jobName -> jobName.startsWith("scene-task-")
                    && !noDealTaskNames.contains(jobName)).collect(Collectors.toList());
            log.info("获取到正在运行的任务:{}",sceneTaskJobNames);
            if (CollectionUtils.isNotEmpty(sceneTaskJobNames)) {
                for (String sceneTaskJobName : sceneTaskJobNames){
                    THREAD_POOL.execute(new Runnable() {
                        @Override
                        public void run() {
                            Long sceneId = getSceneId(sceneTaskJobName);
                            if (sceneId == -1){
                                return;
                            }
                            Long reportId = getReportId(sceneTaskJobName,sceneId);
                            if (reportId == -1){
                                return;
                            }
                            Long customerId = getCustomerId(sceneTaskJobName,sceneId,reportId);
                            if (customerId != null && customerId == 0){
                                return;
                            }
                            SceneManageListResult sceneManage = sceneManageDAO.querySceneManageById(sceneId);
                            if (sceneManage != null && clearSceneStatus.contains(sceneManage.getStatus())){
                                log.info("兜底任务删除匹配到的任务:{},场景状态：{}",sceneTaskJobName,sceneManage.getStatus());
                                String engineInstanceRedisKey = PressureInstanceRedisKey.getEngineInstanceRedisKey(sceneId, reportId,
                                        customerId);
                                ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
                                scheduleStopRequest.setJobName(sceneTaskJobName);
                                scheduleStopRequest.setEngineInstanceRedisKey(engineInstanceRedisKey);
                                engineCallExtApi.deleteJob(scheduleStopRequest);
                            }
                        }
                    });
                }
            }
        }
    }

    private Long getCustomerId(String jobName, Long sceneId, Long reportId) {
        try {
            String tempString = jobName.replace(ScheduleConstants.SCENE_TASK + sceneId + "-" + reportId + "-", "");
            //如果为空说明没有customerId
            if ("".equals(tempString)){
                return null;
            }
            return Long.parseLong(tempString);
        }catch (Exception e){
            log.error("jod名称解析失败，存在类似的jobName:{},异常信息:{}",jobName,e);
            noDealTaskNames.add(jobName);
            return 0L;
        }
    }

    private Long getReportId(String jobName, Long sceneId) {
        try {
            String tempString = jobName.replace(ScheduleConstants.SCENE_TASK + sceneId + "-", "");
            //包含"-"说明有customerId
            if (tempString.contains("-")){
                tempString = tempString.substring(0, tempString.indexOf("-"));
            }
            return Long.parseLong(tempString);
        }catch (Exception e){
            log.error("jod名称解析失败，存在类似的jobName:{},异常信息:{}",jobName,e);
            noDealTaskNames.add(jobName);
            return -1L;
        }
    }

    private Long getSceneId(String jobName){
        try {
            String tempString = jobName.replace(ScheduleConstants.SCENE_TASK, "");
            String substring = tempString.substring(0, tempString.indexOf("-"));
            return Long.parseLong(substring);
        }catch (Exception e){
            log.error("jod名称解析失败，存在类似的jobName:{},异常信息:{}",jobName,e);
            noDealTaskNames.add(jobName);
            return -1L;
        }
    }


    @Override
    public void afterPropertiesSet(){
        //因为清理
        try {
            String[] split = clearTaskSceneStatus.split("-");
            for (String s : split){
                clearSceneStatus.add(Integer.parseInt(s));
            }
        }catch (Exception e){
            log.error("解析清理任务状态失败，请检查配置文件，clear.task.scene.status:{},异常信息:{}",clearTaskSceneStatus,e);
        }

    }

}
