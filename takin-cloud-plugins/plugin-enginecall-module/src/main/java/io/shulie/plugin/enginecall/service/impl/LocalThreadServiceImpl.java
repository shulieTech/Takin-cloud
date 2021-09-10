package io.shulie.plugin.enginecall.service.impl;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import io.shulie.takin.utils.json.JsonHelper;
import org.springframework.stereotype.Service;
import io.shulie.takin.utils.linux.LinuxHelper;
import io.shulie.takin.cloud.common.utils.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.plugin.enginecall.service.EngineCallService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.NoLengthBlockingQueue;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.constants.PressureInstanceRedisKey;

/**
 * @author zhaoyong
 */
@Service
@Slf4j
public class LocalThreadServiceImpl implements EngineCallService {

    @Resource
    private RedisClientUtils redisClientUtils;

    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(1, 6,
        50L, TimeUnit.MILLISECONDS,
        new NoLengthBlockingQueue<>(), new ThreadFactoryBuilder()
        .setNameFormat("local-thread-task-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    private final ConcurrentHashMap<String, Process> shellProcess = new ConcurrentHashMap<>();

    /**
     * 压测引擎包路径
     */
    @Value("${pressure.engine.install.dir:./engine/pressure-engine.tar.gz}")
    private String installDir;
    /**
     * 调度任务路径
     */
    @Value("${pressure.engine.task.dir:./engine}")
    private String taskDir;

    @Override
    public String createJob(Long sceneId, Long taskId, Long customerId) {
        String jobName = ScheduleConstants.getScheduleName(sceneId, taskId, customerId);
        String configMapName = ScheduleConstants.getConfigMapName(sceneId, taskId, customerId);
        if (!new File(installDir).exists()) {
            return "未找到引擎包";
        }
        if (CollectionUtils.isNotEmpty(shellProcess.keySet())) {
            return "本地压测引擎目前只支持启动单个场景！";
        }

        // 删除原有解压包
        String enginePackDir = getEnginePackDir();
        FileUtils.deleteDirectory(enginePackDir);

        // 解压引擎包 ---> 目录结构 /pressure-engine/pressure-engine/bin
        FileUtils.tarGzFileToFile(installDir, enginePackDir);
        String enginePackBin = getEnginePackBin(enginePackDir);
        if (StringUtils.isBlank(enginePackBin)) {
            return "未找到引擎包的解压包";
        }

        //目前只支持单机模式
        THREAD_POOL.execute(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("sh ").append(enginePackDir).append("/pressure-engine/bin/");
            sb.append("start.sh -t \"jmeter\" -c");
            sb.append(" \"").append(taskDir).append("/").append(configMapName).append(" \"");
            sb.append(" -f y ");
            log.info("执行压测包，执行命令如下:{}", sb);
            int state = LinuxHelper.runShell(sb.toString(), null,
                new LinuxHelper.Callback() {
                    @Override
                    public void before(Process process) {
                        log.info("threadPoolExecutor 开始启动压测引擎");
                    }

                    @Override
                    public void after(Process process) {
                        shellProcess.put(jobName, process);
                    }

                    @Override
                    public void exception(Process process, Exception e) {
                        log.error("异常代码【{}】,异常内容：压测引擎启动异常 --> " +
                            "，异常信息: {}", TakinCloudExceptionEnum.SCHEDULE_START_ERROR, e);
                    }
                },
                message -> log.info("执行返回结果:{}", message)
            );
            log.info("jmeter启动" + state);
        });
        // 异步
        return null;
    }

    @Override
    public void deleteJob(String jobName, String engineRedisKey) {
        shellProcess.remove(jobName);
    }

    @Override
    public void createConfigMap(Map<String, Object> configMap, String engineRedisKey) {
        String fileName = (String)configMap.get("name");
        FileUtils.writeTextFile(JsonHelper.obj2StringPretty(configMap.get("engine.conf")), taskDir + "/" + fileName);
        redisClientUtils.hmset(engineRedisKey, PressureInstanceRedisKey.SecondRedisKey.CONFIG_NAME, fileName);
    }

    @Override
    public void deleteConfigMap(String engineRedisKey) {
        Object fileName = redisClientUtils.hmget(engineRedisKey, PressureInstanceRedisKey.SecondRedisKey.CONFIG_NAME);
        String sourceFile = taskDir + "/" + fileName;
        FileUtils.deleteFile(sourceFile);
    }

    @Override
    public List<String> getAllRunningJobName() {
        List<String> running = new ArrayList<>();
        shellProcess.forEach((k, v) -> {
            if (v != null) {
                running.add(k);
            }
        });
        return running;
    }

    @Override
    public String getJobStatus(String jobName) {
        if (shellProcess.get(jobName) == null || !shellProcess.get(jobName).isAlive()) {
            return SceneManageConstant.SCENETASK_JOB_STATUS_NONE;
        }
        return SceneManageConstant.SCENETASK_JOB_STATUS_RUNNING;
    }

    private String getEnginePackBin(String enginePackDir) {
        // 查看 /pressure-engine 下的目录
        //new File(fileDir).listFiles()[1].getPath()
        try {
            File[] files = new File(enginePackDir).listFiles();
            if (files == null || files.length == 0) {
                return null;
            }
            File enginePackFile = Arrays.stream(files).filter(f -> !f.isHidden()).collect(Collectors.toList()).get(0);
            return enginePackFile.getPath();
        } catch (Exception e) {
            return null;
        }

    }

    private String getEnginePackDir() {
        return new File(installDir).getParent() + "/pressure-engine";
    }

}
