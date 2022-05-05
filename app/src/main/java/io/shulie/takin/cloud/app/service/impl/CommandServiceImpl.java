package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.pagehelper.Page;
import cn.hutool.core.util.NumberUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.conf.WatchmanConfig;
import io.shulie.takin.cloud.app.entity.CommandEntity;
import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.constant.enums.CommandType;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.app.service.mapper.CommandMapperService;
import io.shulie.takin.cloud.app.service.mapper.ThreadConfigExampleMapperService;

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
    JobService jobService;
    @Lazy
    @javax.annotation.Resource
    ResourceService resourceService;

    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    WatchmanConfig watchmanConfig;

    @javax.annotation.Resource
    CommandMapperService commandMapperService;
    @javax.annotation.Resource
    ThreadConfigExampleMapperService threadConfigExampleMapperService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void graspResource(long resourceId) {
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(resourceId);
        // 获取资源实例
        List<ResourceExampleEntity> resourceExampleEntityList = resourceService.listExample(resourceEntity.getId());
        // 组装命令内容
        List<HashMap<String, Object>> exampleList = resourceExampleEntityList.stream()
            .map(t -> new HashMap<String, Object>(8) {{
                put("type", 1);
                put("cpu", t.getCpu());
                put("memory", t.getMemory());
                put("limitCpu", t.getLimitCpu());
                put("limitMemory", t.getLimitMemory());
                put("nfsDir", watchmanConfig.getNfsDirectory());
                put("nfsServer", watchmanConfig.getNfsServer());
                put("image", watchmanConfig.getContainerImage());
            }})
            .collect(Collectors.toList());
        HashMap<String, Object> content = new HashMap<String, Object>(2) {{
            put("example", exampleList);
            put("resourceId", resourceId);
        }};
        long commandId = create(resourceEntity.getWatchmanId(), CommandType.GRASP_RESOURCE, jsonService.writeValueAsString(content));
        log.info("下发命令:生成资源实例:{},命令主键{}.", resourceId, commandId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseResource(long resourceId) {
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(resourceId);
        HashMap<String, Object> content = new HashMap<String, Object>(1) {{
            put("resourceId", resourceEntity.getId());
        }};
        long commandId = create(resourceEntity.getWatchmanId(), CommandType.RELEASE_RESOURCE, jsonService.writeValueAsString(content));
        log.info("下发命令:释放资源:{},命令主键{}.", resourceId, commandId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startApplication(long jobId) {
        // TODO 实现
        // 不切分下发
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopApplication(long jobId) {
        // 获取任务
        JobEntity jobEntity = jobService.jobEntity(jobId);
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(jobEntity.getResourceId());
        HashMap<String, Object> content = new HashMap<String, Object>(1) {{
            put("jobId", jobEntity.getId());
            put("resourceId", jobEntity.getResourceId());
        }};
        long commandId = create(resourceEntity.getWatchmanId(), CommandType.STOP_APPLICATION, jsonService.writeValueAsString(content));
        log.info("下发命令:停止任务:{},命令主键{}.", jobId, commandId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateConfig(long jobId) {
        // 获取任务
        JobEntity jobEntity = jobService.jobEntity(jobId);
        if (jobEntity == null) {throw new RuntimeException("未找到任务:" + jobId);}
        // 获取资源
        ResourceEntity resourceEntity = resourceService.entity(jobEntity.getResourceId());
        // 声明命令内容
        List<HashMap<String, Object>> content = new ArrayList<>();
        // 获取所有的线程配置实例
        List<ThreadConfigExampleEntity> threadConfigExampleEntityList = threadConfigExampleMapperService.lambdaQuery()
            .eq(ThreadConfigExampleEntity::getJobId, jobEntity.getId()).list();
        // 根据ref进行分组
        Map<String, List<ThreadConfigExampleEntity>> groupByRef = threadConfigExampleEntityList.stream().collect(Collectors.groupingBy(ThreadConfigExampleEntity::getRef));
        // 根据分组聚合数值
        groupByRef.forEach((k, v) -> {
            // 列出所有的项
            List<HashMap<String, String>> contextList = v.stream().map(t -> {
                try {
                    return jsonService.readValue(t.getContext(), new TypeReference<HashMap<String, String>>() {});
                } catch (JsonProcessingException e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            // 线程数
            int numberSum = contextList.stream().mapToInt(t -> NumberUtil.parseInt(t.getOrDefault("number", "0"))).sum();
            // TPS数
            double tpsSum = contextList.stream().mapToDouble(t -> NumberUtil.parseDouble(t.getOrDefault("tps", "0.0"))).sum();
            // 组装对象
            HashMap<String, Object> contentItem = new HashMap<String, Object>() {{
                put("ref", k);
                put("tps", tpsSum);
                put("number", numberSum);
            }};
            content.add(contentItem);
        });
        // 下发命令
        long commandId = create(resourceEntity.getWatchmanId(), CommandType.MODIFY_THREAD_CONFIG, jsonService.writeValueAsString(content));
        // 输出日志
        log.info("下发命令:更新线程组配置:{},命令主键{}.", jobId, commandId);
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
        CommandEntity commandEntity = new CommandEntity() {{
            setContent(content);
            setType(commandType.getValue());
            setWatchmanId(watchmanId);
        }};
        commandMapperService.save(commandEntity);
        return commandEntity.getId();
    }

    /**
     * 命令确认
     *
     * @param id      命令主键
     * @param message ack内容
     */
    public boolean ack(long id, String type, String message) {
        HashMap<String, String> content = new HashMap<String, String>(2) {{
            put("type", type);
            put("message", message);
        }};
        return commandMapperService.lambdaUpdate()
            .set(CommandEntity::getAckContent, jsonService.writeValueAsString(content))
            .set(CommandEntity::getAckTime, new Date())
            .eq(CommandEntity::getId, id)
            .update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageInfo<CommandEntity> range(long watchmanId, int number) {
        try (Page<?> ignored = PageHelper.startPage(1, number)) {
            List<CommandEntity> list = commandMapperService.lambdaQuery()
                .eq(CommandEntity::getWatchmanId, watchmanId)
                .isNull(CommandEntity::getAckTime)
                .list();
            return new PageInfo<>(list);
        }
    }
}
