package io.shulie.takin.cloud.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.text.CharSequenceUtil;

import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.service.FileService;
import io.shulie.takin.cloud.app.conf.WatchmanConfig;
import io.shulie.takin.cloud.data.entity.CommandEntity;
import io.shulie.takin.cloud.data.entity.MetricsEntity;
import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.constant.enums.CommandType;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.data.entity.FileExampleEntity;
import io.shulie.takin.cloud.app.service.FileExampleService;
import io.shulie.takin.cloud.data.entity.PressureFileEntity;
import io.shulie.takin.cloud.data.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.constant.PressureEngineConstants;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.data.service.CommandMapperService;
import io.shulie.takin.cloud.data.service.MetricsMapperService;
import io.shulie.takin.cloud.data.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.data.service.ThreadConfigMapperService;
import io.shulie.takin.cloud.data.service.PressureFileMapperService;
import io.shulie.takin.cloud.data.service.ThreadConfigExampleMapperService;

/**
 * 命令服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class CommandServiceImpl implements CommandService {

    @Lazy
    @javax.annotation.Resource
    PressureService pressureService;
    @Lazy
    @javax.annotation.Resource
    FileService fileService;
    @Lazy
    @javax.annotation.Resource
    ResourceService resourceService;
    @Lazy
    @javax.annotation.Resource
    FileExampleService fileExampleService;

    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    WatchmanConfig watchmanConfig;

    @javax.annotation.Resource(name = "metricsMapperServiceImpl")
    MetricsMapperService metricsMapper;
    @javax.annotation.Resource(name = "commandMapperServiceImpl")
    CommandMapperService commandMapper;
    @javax.annotation.Resource(name = "pressureFileMapperServiceImpl")
    PressureFileMapperService jobFileMapper;
    @javax.annotation.Resource(name = "threadConfigMapperServiceImpl")
    ThreadConfigMapperService threadConfigMapper;
    @javax.annotation.Resource(name = "threadConfigExampleMapperServiceImpl")
    ThreadConfigExampleMapperService threadConfigExampleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void graspResource(long resourceId) {
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(resourceId);
        if (resourceEntity == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_RESOURCE, resourceId));}
        // 获取资源实例
        List<ResourceExampleEntity> resourceExampleEntityList = resourceService.listExample(resourceEntity.getId());
        // 组装命令内容
        List<Map<String, Object>> exampleList = resourceExampleEntityList.stream()
            .map(t -> {
                Map<String, Object> data = new HashMap<>(10);
                data.put("id", t.getId());
                data.put("cpu", t.getCpu());
                data.put("image", t.getImage());
                data.put("memory", t.getMemory());
                data.put("limitCpu", t.getLimitCpu());
                data.put("watchmanId", t.getWatchmanId());
                data.put("limitMemory", t.getLimitMemory());
                return data;
            })
            .collect(Collectors.toList());
        // 补充index
        long minId = resourceExampleEntityList.stream().mapToLong(ResourceExampleEntity::getId).min().orElse(1);
        for (Map<String, Object> item : exampleList) {
            long id = Long.parseLong(item.get("id").toString());
            item.put("indexNumber", (id - minId) + 1);
        }
        // 生成命令
        Map<String, List<Map<String, Object>>> groupExampleList = exampleList.stream()
            .collect(Collectors.groupingBy(t -> t.get("watchmanId").toString()));
        groupExampleList.forEach((k, v) -> {
            Long watchmanId = Long.valueOf(k);
            // 组装数据
            Map<String, Object> content = new HashMap<>(3);
            content.put("type", 1);
            content.put("example", v);
            content.put(Message.RESOURCE_ID, resourceId);
            long commandId = create(watchmanId, CommandType.GRASP_RESOURCE, jsonService.writeValueAsString(content));
            log.info("下发命令:生成资源实例:{},命令主键{}.", resourceId, commandId);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseResource(long resourceId) {
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(resourceId);
        if (resourceEntity == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_RESOURCE, resourceId));}
        // 请求入参
        Map<String, Object> content = new HashMap<>(1);
        content.put("resourceId", resourceEntity.getId());
        // 下发命令 - 分批次执行
        resourceService.listExample(resourceEntity.getId()).stream().map(ResourceExampleEntity::getWatchmanId)
            .distinct().forEach(t -> {
                long commandId = create(t, CommandType.RELEASE_RESOURCE, jsonService.writeValueAsString(content));
                log.info("下发命令:调度机:{},释放资源:{},命令主键{}.", t, resourceId, commandId);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startApplication(long jobId, Boolean bindByXpathMd5) {
        // 获取任务
        PressureEntity pressureEntity = pressureService.jobEntity(jobId);
        if (pressureEntity == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_JOB, jobId));}
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(pressureEntity.getResourceId());
        // 请求入参
        String content = packageStartJob(jobId, bindByXpathMd5);
        // 下发命令 - 分批次执行
        resourceService.listExample(resourceEntity.getId()).stream().map(ResourceExampleEntity::getWatchmanId)
            .distinct().forEach(t -> {
                long commandId = create(t, CommandType.START_APPLICATION, content);
                log.info("下发命令:启动任务:{},命令主键{}.", jobId, commandId);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopApplication(long jobId) {
        // 获取任务
        PressureEntity pressureEntity = pressureService.jobEntity(jobId);
        if (pressureEntity == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_JOB, jobId));}
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(pressureEntity.getResourceId());
        Map<String, Object> content = new HashMap<>(3);
        content.put("jobId", pressureEntity.getId());
        content.put(Message.TASK_ID, pressureEntity.getId());
        content.put(Message.RESOURCE_ID, pressureEntity.getResourceId());
        // 请求入参
        String request = jsonService.writeValueAsString(content);
        // 下发命令 - 分批次执行
        resourceService.listExample(resourceEntity.getId()).stream().map(ResourceExampleEntity::getWatchmanId)
            .distinct().forEach(t -> {
                long commandId = create(t, CommandType.STOP_APPLICATION, request);
                log.info("下发命令:停止任务:{},命令主键{}.", jobId, commandId);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateConfig(long jobId) {
        // 获取任务
        PressureEntity pressureEntity = pressureService.jobEntity(jobId);
        if (pressureEntity == null) {throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_JOB, jobId));}
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(pressureEntity.getResourceId());
        // 声明命令内容
        List<Map<String, Object>> content = new ArrayList<>();
        // 获取所有的线程配置实例
        List<ThreadConfigExampleEntity> threadConfigExampleEntityList = threadConfigExampleMapper.lambdaQuery()
            .eq(ThreadConfigExampleEntity::getJobId, pressureEntity.getId()).list();
        // 根据ref进行分组
        Map<String, List<ThreadConfigExampleEntity>> groupByRef = threadConfigExampleEntityList
            .stream().collect(Collectors.groupingBy(ThreadConfigExampleEntity::getRef));
        // 根据分组聚合数值
        groupByRef.forEach((k, v) -> {
            // 列出所有的项
            List<Map<String, String>> contextList = new ArrayList<>();
            for (ThreadConfigExampleEntity threadConfigExampleEntity : v) {
                Map<String, String> configExampleContent = jsonService.readValue(threadConfigExampleEntity.getContext(), new TypeReference<Map<String, String>>() {});
                if (configExampleContent != null) {
                    contextList.add(configExampleContent);
                }
            }
            // 线程数
            int numberSum = contextList.stream().mapToInt(t -> NumberUtil.parseInt(t.getOrDefault(Message.THREAD_NUMBER, "0"))).sum();
            // TPS数
            double tpsSum = contextList.stream().mapToDouble(t -> NumberUtil.parseDouble(t.getOrDefault(Message.TPS_NUMBER, "0.0"))).sum();
            // 组装对象
            Map<String, Object> contentItem = new HashMap<>(3);
            contentItem.put("ref", k);
            contentItem.put("tps", tpsSum);
            contentItem.put(PressureEngineConstants.THREAD_GROUP_CONCURRENT_NUMBER, numberSum);
            content.add(contentItem);
        });
        Map<String, Object> result = new HashMap<>(3);
        result.put("content", content);
        result.put("jobId", pressureEntity.getId());
        result.put("taskId", pressureEntity.getId());
        // 下发命令
        String request = jsonService.writeValueAsString(result);
        // 下发命令 - 分批次执行
        resourceService.listExample(resourceEntity.getId()).stream().map(ResourceExampleEntity::getWatchmanId)
            .distinct().forEach(t -> {
                long commandId = create(t, CommandType.MODIFY_THREAD_CONFIG, request);
                log.info("下发命令:更新线程组配置:{},命令主键{}.", jobId, commandId);
            });
        // 输出日志
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void announceFile(long fileId) {
        // 获取到所有的文件实例
        List<FileExampleEntity> exampleEntityList = fileExampleService.listExample(fileId);
        // 按调度器分组
        Map<Long, List<FileExampleEntity>> groupData = exampleEntityList.stream()
            .collect(Collectors.groupingBy(FileExampleEntity::getWatchmanId));
        // 分调度器下发
        groupData.forEach((k, v) -> create(k, CommandType.ANNOUNCE_FILE, jsonService.writeValueAsString(v)));
    }

    /**
     * 命令入库
     *
     * @param watchmanId  调度主键
     * @param commandType 命令类型
     * @param content     命令内容容
     * @return 命令主键
     */
    private long create(Long watchmanId, CommandType commandType, String content) {
        CommandEntity commandEntity = new CommandEntity()
            .setContent(content)
            .setWatchmanId(watchmanId)
            .setType(commandType.getValue());
        commandMapper.save(commandEntity);
        return commandEntity.getId();
    }

    /**
     * {@inheritDoc}
     */
    public boolean ack(long id, String type, String message) {
        Map<String, String> content = new HashMap<>(2);
        content.put("type", type);
        content.put("message", message);
        return commandMapper.lambdaUpdate()
            .set(CommandEntity::getAckContent, jsonService.writeValueAsString(content))
            .set(CommandEntity::getAckTime, new Date())
            .eq(CommandEntity::getId, id)
            .update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageInfo<CommandEntity> range(long watchmanId, int number, CommandType type) {
        try (Page<?> ignored = PageMethod.startPage(1, number)) {
            List<CommandEntity> list = commandMapper.lambdaQuery()
                .eq(type != null, CommandEntity::getType, type == null ? null : type.getValue())
                .eq(CommandEntity::getWatchmanId, watchmanId)
                .isNull(CommandEntity::getAckTime)
                .list();
            return new PageInfo<>(list);
        }
    }

    /**
     * 打包启动任务参数
     *
     * @param jobId 任务主键
     * @return 启动任务参数
     */
    public String packageStartJob(long jobId, Boolean bindByXpathMd5) {
        // 任务
        PressureEntity pressureEntity = pressureService.jobEntity(jobId);
        // 线程组配置
        List<ThreadConfigEntity> threadConfigEntityList =
            threadConfigMapper.lambdaQuery().eq(ThreadConfigEntity::getJobId, jobId).list();
        // 压测指标配置
        List<MetricsEntity> metricsEntityList = metricsMapper.lambdaQuery().eq(MetricsEntity::getJobId, jobId).list();
        Map<String, Object> basicConfig = new HashMap<>(32);
        basicConfig.put("taskId", jobId);
        basicConfig.put("pressureType", pressureEntity.getType());
        basicConfig.put("resourceId", pressureEntity.getResourceId());
        basicConfig.put("memSetting", pressureEntity.getStartOption());
        basicConfig.put("continuedTime", pressureEntity.getDuration());
        basicConfig.put("traceSampling", pressureEntity.getSampling());
        basicConfig.put("zkServers", watchmanConfig.getZkAddress());
        basicConfig.put("logQueueSize", watchmanConfig.getLogQueueSize());
        basicConfig.put("backendQueueCapacity", watchmanConfig.getBackendQueueCapacity());
        basicConfig.put("tpsTargetLevelFactor", watchmanConfig.getTpsTargetLevelFactor());
        Map<Object, Object> ptlLogConfig = new HashMap<>(6);
        ptlLogConfig.put("logCutOff", watchmanConfig.getLogCutOff());
        ptlLogConfig.put("ptlFileEnable", watchmanConfig.getPtlFileEnable());
        ptlLogConfig.put("ptlUploadFrom", watchmanConfig.getPtlUploadFrom());
        ptlLogConfig.put("ptlFileErrorOnly", watchmanConfig.getPtlFileErrorOnly());
        ptlLogConfig.put("timeoutThreshold", watchmanConfig.getTimeoutThreshold());
        ptlLogConfig.put("ptlFileTimeoutOnly", watchmanConfig.getPtlFileTimeoutOnly());
        basicConfig.put("ptlLogConfig", ptlLogConfig);
        // 最大线程数 (以前是直接传null的，韵达改了一个版本，传入具体值， 但是有问题，所以还是传null)
        basicConfig.put("maxThreadNum", null);
        // 固定是0的
        basicConfig.put("tpsThreadMode", 0);
        // 现在没有办法区分版本
        basicConfig.put("bindByXpathMd5", Objects.isNull(bindByXpathMd5) || bindByXpathMd5);
        // 以前的文件里面没有用到
        basicConfig.put("tpsTargetLevel", null);
        // 填充文件
        // 获取所有文件
        List<Map<String, Object>> dataFileList = packageStartJobWhitJobFile(pressureEntity.getId(), pressureEntity.getResourceExampleNumber());
        basicConfig.put("dataFileList", dataFileList);
        // 压测指标配置
        Map<String, Map<String, Object>> businessMap = packageStartJobWhitBusinessMap(metricsEntityList);
        basicConfig.put("businessMap", businessMap);
        // 线程组配置
        Map<String, Map<String, Object>> threadGroupConfigMap = packageStartJobWhitThreadGroupConfig(threadConfigEntityList);
        basicConfig.put("threadGroupConfigMap", threadGroupConfigMap);
        // 如果是试跑(脚本调试)
        Map<String, String> ext = packageStartJobTryRun(threadConfigEntityList);
        if (!ext.isEmpty()) {basicConfig.putAll(ext);}
        // 如果是TPS模式
        boolean isTps = threadConfigEntityList.stream().anyMatch(t -> ThreadGroupType.TPS.getCode().equals(t.getMode()));
        if (isTps) {
            int tpsTargetLevel = metricsEntityList.stream()
                // 解析content
                .map(t -> jsonService.readValue(t.getContext(), new TypeReference<Map<String, String>>() {}))
                // 返回tps
                .map(t -> t.getOrDefault("tps", "0"))
                // 空值处理
                .map(t -> t == null ? "0" : t)
                // 类型转换
                .mapToInt(Integer::parseInt)
                // 汇总
                .sum();
            basicConfig.put("tpsTargetLevel", tpsTargetLevel);
        }
        // 输出成JSON
        return jsonService.writeValueAsString(basicConfig);
    }

    /**
     * 组装启动任务命令-文件
     *
     * @param jobId  任务主键
     * @param number 任务实例数
     * @return 文件信息
     */
    private List<Map<String, Object>> packageStartJobWhitJobFile(long jobId, int number) {
        List<PressureFileEntity> pressureFileEntityList = jobFileMapper.lambdaQuery().eq(PressureFileEntity::getJobId, jobId).list();
        Map<String, List<PressureFileEntity>> fileInfo = pressureFileEntityList.stream().collect(Collectors.groupingBy(PressureFileEntity::getUri));
        List<Map<String, Object>> dataFileList = new ArrayList<>();
        fileInfo.forEach((k, v) -> {
            PressureFileEntity info = v.get(0);
            boolean split = !Long.valueOf(-1).equals(info.getStartPoint()) && !Long.valueOf(-1).equals(info.getEndPoint());
            Map<String, List<Map<String, Object>>> splitInfo = new HashMap<>(number);
            if (split) {
                for (int i = 0; i < number; i++) {
                    List<Map<String, Object>> itemSplitInfo = new ArrayList<>(v.size());
                    for (int j = 0, vSize = v.size(); j < vSize; j++) {
                        PressureFileEntity t = v.get(j);
                        Map<String, Object> config = new HashMap<>(4);
                        config.put("partition", j);
                        config.put("end", t.getEndPoint());
                        config.put("start", t.getStartPoint());
                        itemSplitInfo.add(config);
                    }
                    splitInfo.put(String.valueOf(i), itemSplitInfo);
                }
            }
            Map<String, Object> config = new HashMap<>(16);
            config.put("split", split);
            config.put("refId", null);
            config.put("ordered", null);
            config.put("fileMd5", null);
            config.put("isBigFile", null);
            config.put("path", info.getUri());
            config.put("type", info.getType());
            config.put("startEndPositions", splitInfo);
            config.put("name", FileUtil.getName(info.getUri()));
            dataFileList.add(config);
        });
        return dataFileList;
    }

    /**
     * 组装启动任务命令-压测指标-目标
     *
     * @param metricsEntityList 压测指标-目标实体 集合
     * @return 压测指标-目标
     */
    private Map<String, Map<String, Object>> packageStartJobWhitBusinessMap(List<MetricsEntity> metricsEntityList) {
        Map<String, Map<String, Object>> businessMap = new HashMap<>(metricsEntityList.size());
        metricsEntityList.forEach(t -> {
            Map<String, Object> metrics = new HashMap<>(5);
            try {
                Map<String, String> context = jsonService.readValue(t.getContext(), new TypeReference<Map<String, String>>() {});
                metrics.putAll(context);
                metrics.put("bindRef", t.getRef());
                metrics.put("activityName", t.getRef());
                metrics.put("rate", context.get("successRate"));
            } catch (RuntimeException e) {
                log.error("JSON反序列化失败", e);
            }
            businessMap.put(t.getRef(), metrics);
        });
        return businessMap;
    }

    /**
     * 组装启动任务命令-线程组配置
     *
     * @param threadConfigEntityList 线程组配置实体 集合
     * @return 线程组配置
     */
    private Map<String, Map<String, Object>> packageStartJobWhitThreadGroupConfig(List<ThreadConfigEntity> threadConfigEntityList) {
        Map<String, Map<String, Object>> threadGroupConfigMap = new HashMap<>(threadConfigEntityList.size());
        threadConfigEntityList.forEach(t -> {
            try {
                Map<String, String> context = jsonService.readValue(t.getContext(), new TypeReference<Map<String, String>>() {});
                ThreadGroupType threadGroupType = ThreadGroupType.of(t.getMode());
                Map<String, Object> threadConfig = new HashMap<>(6);
                threadConfig.put("rampUpUnit", "s");
                threadConfig.put("estimateFlow", null);
                threadConfig.put("steps", context.get("step"));
                threadConfig.put("type", threadGroupType.getType());
                threadConfig.put("mode", threadGroupType.getModel());
                threadConfig.put("threadNum", context.get(PressureEngineConstants.THREAD_GROUP_CONCURRENT_NUMBER));
                threadConfig.put("rampUp", context.get("growthTime"));
                threadGroupConfigMap.put(t.getRef(), threadConfig);
            } catch (RuntimeException e) {
                log.error("JSON反序列化失败", e);
            }
        });
        return threadGroupConfigMap;
    }

    /**
     * 试跑模式(脚本调试)-试跑模式(脚本调试)拓展
     *
     * @param threadConfigEntityList 线程组配置
     * @return 拓展配置
     */
    private Map<String, String> packageStartJobTryRun(List<ThreadConfigEntity> threadConfigEntityList) {
        Map<String, String> context = new HashMap<>(0);
        String loopsNum = "loopsNum";
        String expectThroughput = "expectThroughput";
        context.put(loopsNum, null);
        context.put(expectThroughput, null);
        threadConfigEntityList.forEach(t -> {
            Integer modeCode = t.getMode();
            ThreadGroupType threadGroupType = ThreadGroupType.of(modeCode);
            if (threadGroupType.equals(ThreadGroupType.TRY_RUN)) {
                Map<String, String> threadConfigContext = jsonService.readValue(t.getContext(), new TypeReference<Map<String, String>>() {});
                context.put(loopsNum, threadConfigContext.get(loopsNum));
                threadConfigContext.put(PressureEngineConstants.THREAD_GROUP_CONCURRENT_NUMBER, threadConfigContext.get(expectThroughput));
                context.put(expectThroughput, threadConfigContext.get(expectThroughput));
                t.setContext(jsonService.writeValueAsString(threadConfigContext));
            }
        });
        return context;
    }
}