package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.entity.SlaEntity;
import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.mapper.JobMapper;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.constant.enums.FileType;
import io.shulie.takin.cloud.model.response.JobConfig;
import io.shulie.takin.cloud.app.entity.JobFileEntity;
import io.shulie.takin.cloud.app.entity.MetricsEntity;
import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.model.request.StartRequest;
import io.shulie.takin.cloud.model.request.ModifyConfig;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.app.service.JobExampleService;
import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.service.ResourceExampleService;
import io.shulie.takin.cloud.model.request.StartRequest.SlaInfo;
import io.shulie.takin.cloud.model.request.StartRequest.FileInfo;
import io.shulie.takin.cloud.app.service.mapper.SlaMapperService;
import io.shulie.takin.cloud.app.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.model.request.StartRequest.MetricsInfo;
import io.shulie.takin.cloud.app.service.mapper.MetricsMapperService;
import io.shulie.takin.cloud.app.service.mapper.JobFileMapperService;
import io.shulie.takin.cloud.app.service.mapper.JobExampleMapperService;
import io.shulie.takin.cloud.model.request.StartRequest.ThreadConfigInfo;
import io.shulie.takin.cloud.app.service.mapper.ThreadConfigMapperService;
import io.shulie.takin.cloud.app.service.mapper.ThreadConfigExampleMapperService;

/**
 * 任务服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class JobServiceImpl implements JobService {
    @javax.annotation.Resource
    JobMapper jobMapper;
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    ResourceService resourceService;
    @javax.annotation.Resource
    SlaMapperService slaMapperService;
    @javax.annotation.Resource
    JobExampleService jobExampleService;
    @javax.annotation.Resource
    JobConfigServiceImpl jobConfigService;
    @javax.annotation.Resource
    JobFileMapperService jobFileMapperService;
    @javax.annotation.Resource
    MetricsMapperService metricsMapperService;
    @javax.annotation.Resource
    ResourceExampleService resourceExampleService;
    @javax.annotation.Resource
    JobExampleMapperService jobExampleMapperService;
    @javax.annotation.Resource
    ThreadConfigMapperService threadConfigMapperService;
    @javax.annotation.Resource
    ThreadConfigExampleMapperService threadConfigExampleMapperService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String start(StartRequest jobInfo) {
        ResourceEntity resourceEntity = resourceService.entity(jobInfo.getResourceId());
        JobEntity jobEntity = startFillJob(resourceEntity.getId(), resourceEntity.getNumber(), jobInfo);
        jobMapper.insert(jobEntity);
        // 填充job实例
        List<JobExampleEntity> jobExampleEntityList = startFillJobExample(jobEntity.getId(), jobEntity.getDuration(), resourceEntity.getId(), resourceEntity.getNumber());
        jobExampleMapperService.saveBatch(jobExampleEntityList);
        // 填充线程组配置
        List<ThreadConfigEntity> threadConfigEntityList = startFillThreadConfig(jobInfo, jobEntity);
        threadConfigMapperService.saveBatch(threadConfigEntityList);
        // 填充线程配置实例
        List<ThreadConfigExampleEntity> threadConfigExampleEntityList = startFillThreadConfigExample(jobEntity.getId(), jobEntity.getResourceExampleNumber(), jobInfo, jobExampleEntityList);
        threadConfigExampleMapperService.saveBatch(threadConfigExampleEntityList);
        // 填充SLA配置
        List<SlaEntity> slaEntityList = startFillSla(jobInfo, jobEntity);
        slaMapperService.saveBatch(slaEntityList);
        // 切分、填充任务文件
        List<JobFileEntity> jobFileEntityList = startFillJobFile(jobEntity.getId(), jobInfo, jobExampleEntityList);
        jobFileMapperService.saveBatch(jobFileEntityList);
        // 指标目标
        List<MetricsEntity> metricsEntityList = startFillMetrics(jobEntity.getId(), jobInfo);
        metricsMapperService.saveBatch(metricsEntityList);
        // 下发启动命令
        commandService.startApplication(jobEntity.getId());
        // 返回任务主键
        return String.valueOf(jobEntity.getId());
    }

    /**
     * 填充任务实体
     *
     * @param jobInfo               任务信息
     * @param resourceId            资源主键
     * @param resourceExampleNumber 资源实例数量
     * @return 任务实体
     */
    private JobEntity startFillJob(long resourceId, int resourceExampleNumber, StartRequest jobInfo) {
        // 时长取最大值
        Integer duration = jobInfo.getThreadConfig()
            .stream().map(ThreadConfigInfo::getDuration)
            .max(Comparator.naturalOrder()).orElse(0);
        return new JobEntity()
            .setResourceId(resourceId)
            .setName(jobInfo.getName())
            .setDuration(duration)
            .setSampling(jobInfo.getSampling())
            .setType(jobInfo.getType().getCode())
            .setCallbackUrl(jobInfo.getCallbackUrl())
            .setResourceExampleNumber(resourceExampleNumber);
    }

    /**
     * 填充任务实例
     *
     * @param jobId          任务主键
     * @param duration       持续时长
     * @param resourceId     资源主键
     * @param resourceNumber 资源需要生成的实例数量
     * @return 任务实例
     */
    private List<JobExampleEntity> startFillJobExample(long jobId, int duration, long resourceId, int resourceNumber) {
        List<JobExampleEntity> jobExampleEntityList = new ArrayList<>(resourceNumber);
        List<ResourceExampleEntity> resourceExampleEntityList =
            resourceService.listExample(resourceId);
        for (int i = 0; i < resourceNumber; i++) {
            jobExampleEntityList.add(new JobExampleEntity()
                .setJobId(jobId)
                .setNumber(i + 1)
                .setDuration(duration)
                .setResourceExampleId(resourceExampleEntityList.get(i).getId())
            );
        }
        return jobExampleEntityList;
    }

    /**
     * 填充线程组配置
     *
     * @param jobInfo   任务信息
     * @param jobEntity 任务实体
     * @return 线程组配置
     */
    private List<ThreadConfigEntity> startFillThreadConfig(StartRequest jobInfo, JobEntity jobEntity) {
        List<ThreadConfigEntity> threadConfigEntityList = new ArrayList<>(0);
        for (ThreadConfigInfo threadConfigInfo : jobInfo.getThreadConfig()) {
            ThreadConfigEntity threadConfigEntity = new ThreadConfigEntity()
                .setJobId(jobEntity.getId())
                .setMode(threadConfigInfo.getType().getCode())
                .setRef(threadConfigInfo.getRef());
            HashMap<String, Object> context = threadConfigInfo(threadConfigInfo);
            if (jobInfo.getExt() != null) {context.putAll(jobInfo.getExt());}
            threadConfigEntity.setContext(jsonService.writeValueAsString(context));
            threadConfigEntityList.add(threadConfigEntity);
        }
        return threadConfigEntityList;
    }

    /**
     * 启动任务 - 填充线程配置实例
     *
     * @param jobId                任务主键
     * @param jobInfo              任务信息
     * @param jobExampleEntityList 任务实例实体
     * @param number               每个线程组配置对应的实例数量
     * @return 线程配置实例
     */
    private List<ThreadConfigExampleEntity> startFillThreadConfigExample(long jobId, int number, StartRequest jobInfo,
        List<JobExampleEntity> jobExampleEntityList) {
        // 切分线程配置
        List<List<ThreadConfigInfo>> threadExampleList = splitThreadConfig(jobInfo.getThreadConfig(), number);
        // 组装返回值
        List<ThreadConfigExampleEntity> threadConfigExampleEntityList = new ArrayList<>(number);
        for (int i = 0; i < threadExampleList.size(); i++) {
            JobExampleEntity jobExampleEntity = jobExampleEntityList.get(i);
            List<ThreadConfigInfo> threadConfigInfoList = threadExampleList.get(i);
            for (int j = 0; j < threadConfigInfoList.size(); j++) {
                ThreadConfigInfo t = threadConfigInfoList.get(j);
                HashMap<String, Object> context = threadConfigInfo(t);
                if (jobInfo.getExt() != null) {context.putAll(jobInfo.getExt());}
                threadConfigExampleEntityList.add(new ThreadConfigExampleEntity()
                    .setJobId(jobId)
                    .setRef(t.getRef())
                    .setSerialNumber(j)
                    .setType(t.getType().getCode())
                    .setJobExampleId(jobExampleEntity.getId())
                    .setContext(jsonService.writeValueAsString(context))
                );
            }
        }
        return threadConfigExampleEntityList;
    }

    /**
     * 填充SLA
     *
     * @param jobInfo   任务信息
     * @param jobEntity 任务实体
     * @return SLA
     */
    private List<SlaEntity> startFillSla(StartRequest jobInfo, JobEntity jobEntity) {
        List<SlaEntity> slaEntityList = new ArrayList<>(jobInfo.getSlaConfig().size());
        for (int i = 0; i < jobInfo.getSlaConfig().size(); i++) {
            SlaInfo slaInfo = jobInfo.getSlaConfig().get(i);
            SlaEntity slaEntity = new SlaEntity()
                .setRef(slaInfo.getRef())
                .setJobId(jobEntity.getId())
                .setAttach(slaInfo.getAttach())
                .setFormulaNumber(slaInfo.getFormulaNumber())
                .setFormulaTarget(slaInfo.getFormulaTarget().getCode())
                .setFormulaSymbol(slaInfo.getFormulaSymbol().getCode());
            slaEntityList.add(slaEntity);
        }
        return slaEntityList;
    }

    /**
     * 填充任务文件
     *
     * @param jobId                任务主键
     * @param jobInfo              任务信息
     * @param jobExampleEntityList 任务实例实体
     * @return 任务文件
     */
    private List<JobFileEntity> startFillJobFile(long jobId, StartRequest jobInfo, List<JobExampleEntity> jobExampleEntityList) {
        List<JobFileEntity> jobFileEntityList = new ArrayList<>();
        for (int i = 0; i < jobExampleEntityList.size(); i++) {
            JobExampleEntity jobExampleEntity = jobExampleEntityList.get(i);
            // 脚本文件
            jobFileEntityList.add(new JobFileEntity()
                .setJobId(jobId)
                .setEndPoint(-1L)
                .setStartPoint(-1L)
                .setType(FileType.SCRIPT.getCode())
                .setUri(jobInfo.getScriptFile().getUri())
                .setJobExampleId(jobExampleEntity.getId()));
            // 数据文件
            List<FileInfo> dataFile = jobInfo.getDataFile() == null ? new ArrayList<>() : jobInfo.getDataFile();
            for (FileInfo fileInfo : dataFile) {
                jobFileEntityList.add(new JobFileEntity()
                    .setJobId(jobId)
                    .setUri(fileInfo.getUri())
                    .setType(FileType.DATA.getCode())
                    .setJobExampleId(jobExampleEntity.getId())
                    .setEndPoint(fileInfo.getSplitList().get(i).getEnd())
                    .setStartPoint(fileInfo.getSplitList().get(i).getStart()));
            }
            // 依赖文件
            List<FileInfo> dependencyFile = (jobInfo.getDependencyFile() == null ? new ArrayList<>(0) : jobInfo.getDependencyFile());
            dependencyFile.forEach(t -> jobFileEntityList.add(new JobFileEntity()
                .setJobId(jobId)
                .setEndPoint(-1L)
                .setStartPoint(-1L)
                .setUri(t.getUri())
                .setType(FileType.ATTACHMENT.getCode())
                .setJobExampleId(jobExampleEntity.getId())));
        }
        return jobFileEntityList;
    }

    /**
     * 填充指标信息
     *
     * @param jobId   任务主键
     * @param jobInfo 任务信息
     * @return 指标信息
     */
    private List<MetricsEntity> startFillMetrics(long jobId, StartRequest jobInfo) {
        List<MetricsEntity> metricsEntityList = new ArrayList<>();
        for (int i = 0; i < jobInfo.getMetricsConfig().size(); i++) {
            MetricsInfo metricsInfo = jobInfo.getMetricsConfig().get(i);
            String context = null;
            try {
                HashMap<String, Object> contextObject = new HashMap<>(4);
                contextObject.put("sa", metricsInfo.getSa());
                contextObject.put("rt", metricsInfo.getRt());
                contextObject.put("tps", metricsInfo.getTps());
                contextObject.put("successRate", metricsInfo.getSuccessRate());
                context = jsonService.writeValueAsString(contextObject);
            } catch (Exception e) {
                log.warn("JSON序列化失败");
            }
            String finalContext = context;
            metricsEntityList.add(
                new MetricsEntity()
                    .setJobId(jobId)
                    .setContext(finalContext)
                    .setRef(metricsInfo.getRef()));
        }
        return metricsEntityList;
    }

    /**
     * 转换线程配置信息
     *
     * @param threadConfigInfo 线程配置信息
     * @return 转换后的Map
     */
    private HashMap<String, Object> threadConfigInfo(ThreadConfigInfo threadConfigInfo) {
        HashMap<String, Object> content = new HashMap<>(5);
        content.put("number", threadConfigInfo.getNumber());
        content.put("tps", threadConfigInfo.getTps());
        content.put("duration", threadConfigInfo.getDuration());
        content.put("growthTime", threadConfigInfo.getGrowthTime());
        content.put("step", threadConfigInfo.getGrowthStep());
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(long jobId) {
        // 获取任务
        JobEntity jobEntity = jobMapper.selectById(jobId);
        if (jobEntity == null) {
            throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_JOB, jobId));
        }
        // 释放资源
        commandService.releaseResource(jobEntity.getResourceId());
        // 停止任务
        commandService.stopApplication(jobEntity.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JobConfig> getConfig(long jobId, String ref) {
        List<ThreadConfigExampleEntity> threadConfigExampleEntity = jobConfigService.threadExampleItem(jobId, ref);
        return threadConfigExampleEntity.stream().map(t -> {
            ThreadConfigInfo context = null;
            try {
                context = jsonService.readValue(t.getContext(), new TypeReference<ThreadConfigInfo>() {});
            } catch (RuntimeException e) {
                log.warn("线程组配置实例context解析失败");
            }
            ThreadConfigInfo finalContext = context;
            return new JobConfig()
                .setRef(t.getRef())
                .setJobId(t.getJobId())
                .setContext(finalContext)
                .setType(ThreadGroupType.of(t.getType()));
        }).collect(Collectors.toList());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyConfig(long jobId, ModifyConfig context) {
        // 1. 找到要修改的配置项
        List<ThreadConfigExampleEntity> threadConfigExampleEntity = jobConfigService.threadExampleItem(jobId, context.getRef());
        // 2. 如果没有抛出异常
        if (CollUtil.isEmpty(threadConfigExampleEntity)) {
            throw new IllegalArgumentException("未找到可修改的配置");
        }
        // 存在即修改
        else {
            // TOOD 重新切分
            List<List<ThreadConfigInfo>> splitThreadConfig =
                splitThreadConfig(CollUtil.toList(context.getContext()), threadConfigExampleEntity.size());
            for (int i = 0; i < splitThreadConfig.get(0).size(); i++) {
                String contextString = jsonService.writeValueAsString(splitThreadConfig.get(0).get(i));
                // 2.1 更新任务配置实例项
                jobConfigService.modifThreadConfigExample(
                    threadConfigExampleEntity.get(i).getId(),
                    context.getType(),
                    contextString);
            }
        }
        // 2.2 下发命令
        commandService.updateConfig(jobId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobEntity jobEntity(long jobId) {
        return jobMapper.selectById(jobId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobExampleEntity jobExampleEntity(long jobExampleId) {
        return jobExampleMapperService.getById(jobExampleId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JobExampleEntity> jobExampleEntityList(long jobId) {
        return jobExampleMapperService.lambdaQuery()
            .eq(JobExampleEntity::getJobId, jobId)
            .list();
    }

    @Override
    public void onStart(long id) {
        jobExampleEntityList(id).forEach(t -> jobExampleService.onStart(t.getId()));
    }

    @Override
    public void onStop(long id) {
        jobExampleEntityList(id).forEach(t -> {
            // 停止任务实例
            jobExampleService.onStop(t.getId());
            // 停止任务实例对应的资源实例
            resourceExampleService.onStop(t.getResourceExampleId());
        });
    }

    /**
     * 切分线程组配置
     *
     * @param threadConfigInfoList 要切分的数量
     * @return 切分后的线程组配置
     */
    private List<List<ThreadConfigInfo>> splitThreadConfig(List<ThreadConfigInfo> threadConfigInfoList, int size) {
        List<List<ThreadConfigInfo>> result = new ArrayList<>(threadConfigInfoList.size());
        for (ThreadConfigInfo t : threadConfigInfoList) {
            List<ThreadConfigInfo> itemResult = new ArrayList<>(size);
            List<Integer> tpsList = splitInteger(t.getTps() == null ? 0 : t.getTps(), size);
            List<Integer> numberList = splitInteger(t.getNumber() == null ? 0 : t.getNumber(), size);
            for (int j = 0; j < size; j++) {
                itemResult.add(new ThreadConfigInfo()
                    .setRef(t.getRef())
                    .setType(t.getType())
                    .setTps(tpsList.get(j))
                    .setDuration(t.getDuration())
                    .setNumber(numberList.get(j))
                    .setGrowthStep(t.getGrowthStep())
                    .setGrowthTime(t.getGrowthTime())
                );
            }
            result.add(itemResult);
        }
        return result;
    }

    /**
     * 切分数值
     * <p>余数平分到每一项</p>
     *
     * @param value 需要分隔的值
     * @param size  分隔的份数
     * @return 结果集合
     */
    private List<Integer> splitInteger(int value, int size) {
        List<Integer> result = new ArrayList<>(size);
        int quotient = value / size;
        int remainder = value % size;
        if (quotient == 0 && remainder != 0) {
            throw new NumberFormatException(CharSequenceUtil.format("无法把{}分隔成{}份", value, size));
        }
        // 处理商
        for (int i = 0; i < size; i++) {result.add(quotient);}
        // 处理余数
        for (int i = 0; i < remainder; i++) {result.set(i, result.get(i) + 1);}
        return result;
    }
}
