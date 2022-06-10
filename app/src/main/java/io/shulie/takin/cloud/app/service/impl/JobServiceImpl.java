package io.shulie.takin.cloud.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.apache.commons.lang3.StringUtils;
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
        // 获取资源
        ResourceEntity resource = resourceService.entity(jobInfo.getResourceId());
        // 生成任务
        JobEntity job = startFillJob(resource.getId(), resource.getNumber(), jobInfo);
        jobMapper.insert(job);
        // 填充job实例
        List<JobExampleEntity> jobExample = startFillJobExample(job.getId(), job.getDuration(), resource.getId(), resource.getNumber());
        jobExampleMapperService.saveBatch(jobExample);
        // 填充线程组配置
        List<ThreadConfigEntity> threadConfig = startFillThreadConfig(job.getId(), jobInfo);
        threadConfigMapperService.saveBatch(threadConfig);
        // 填充线程配置实例
        List<ThreadConfigExampleEntity> threadConfigExample = startFillThreadConfigExample(job.getId(), jobInfo, jobExample);
        threadConfigExampleMapperService.saveBatch(threadConfigExample);
        // 填充SLA配置
        List<SlaEntity> slaList = startFillSla(job.getId(), jobInfo.getSlaConfig());
        slaMapperService.saveBatch(slaList);
        // 切分、填充任务文件
        List<JobFileEntity> jobFileList = startFillJobFile(job.getId(), jobInfo, jobExample);
        jobFileMapperService.saveBatch(jobFileList);
        // 指标目标
        List<MetricsEntity> metricsList = startFillMetrics(job.getId(), jobInfo.getMetricsConfig());
        metricsMapperService.saveBatch(metricsList);
        // 下发启动命令
        commandService.startApplication(job.getId(), jobInfo.getBindByXpathMd5());
        // 返回任务主键
        return String.valueOf(job.getId());
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
                .setStartOption(jobInfo.getJvmOptions())
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
        List<ResourceExampleEntity> resourceExampleEntityList = resourceService.listExample(resourceId);
        return IntStream.range(0, resourceNumber).mapToObj(t -> new JobExampleEntity()
                .setJobId(jobId)
                .setNumber(t + 1)
                .setDuration(duration)
                .setResourceExampleId(resourceExampleEntityList.get(t).getId())).collect(Collectors.toList());
    }

    /**
     * 填充线程组配置
     *
     * @param jobInfo 任务信息
     * @param jobId   任务主键
     * @return 线程组配置
     */
    private List<ThreadConfigEntity> startFillThreadConfig(long jobId, StartRequest jobInfo) {
        return jobInfo.getThreadConfig().stream().map(threadConfigInfo -> {
            Map<String, Object> context = threadConfigInfo(threadConfigInfo);
            if (jobInfo.getExt() != null) {
                context.putAll(jobInfo.getExt());
            }
            return new ThreadConfigEntity()
                    .setJobId(jobId)
                    .setRef(threadConfigInfo.getRef())
                    .setMode(threadConfigInfo.getType().getCode())
                    .setContext(jsonService.writeValueAsString(context));
        }).collect(Collectors.toList());
    }

    /**
     * 启动任务 - 填充线程配置实例
     *
     * @param jobId      任务主键
     * @param jobInfo    任务信息
     * @param jobExample 任务实例实体
     * @return 线程配置实例
     */
    private List<ThreadConfigExampleEntity> startFillThreadConfigExample(long jobId, StartRequest jobInfo, List<JobExampleEntity> jobExample) {
        // 切分线程配置
        List<List<ThreadConfigInfo>> splitResult = splitThreadConfig(jobInfo.getThreadConfig(), jobExample.size());
        // 组装返回值
        List<ThreadConfigExampleEntity> threadConfigExample = new ArrayList<>(jobExample.size());
        IntStream.range(0, splitResult.size()).forEach(t -> {
            JobExampleEntity jobExampleEntity = jobExample.get(t);
            List<ThreadConfigInfo> threadConfigInfoList = splitResult.get(t);
            IntStream.range(0, threadConfigInfoList.size()).mapToObj(c -> {
                ThreadConfigInfo z = threadConfigInfoList.get(c);
                Map<String, Object> context = threadConfigInfo(z);
                if (jobInfo.getExt() != null) {
                    context.putAll(jobInfo.getExt());
                }
                return new ThreadConfigExampleEntity()
                        .setJobId(jobId)
                        .setRef(z.getRef())
                        .setSerialNumber(c)
                        .setType(z.getType().getCode())
                        .setJobExampleId(jobExampleEntity.getId())
                        .setContext(jsonService.writeValueAsString(context));
            }).forEach(threadConfigExample::add);
        });
        return threadConfigExample;
    }

    /**
     * 填充SLA
     *
     * @param jobId       任务主键
     * @param slaInfoList SLA信息
     * @return SLA
     */
    private List<SlaEntity> startFillSla(long jobId, List<SlaInfo> slaInfoList) {
        return slaInfoList.stream().map(t -> new SlaEntity()
                .setJobId(jobId)
                .setRef(t.getRef())
                .setAttach(t.getAttach())
                .setFormulaNumber(t.getFormulaNumber())
                .setFormulaTarget(t.getFormulaTarget().getCode())
                .setFormulaSymbol(t.getFormulaSymbol().getCode())).collect(Collectors.toList());
    }

    /**
     * 填充任务文件
     *
     * @param jobId          任务主键
     * @param jobInfo        任务信息
     * @param jobExampleList 任务实例实体
     * @return 任务文件
     */
    private List<JobFileEntity> startFillJobFile(long jobId, StartRequest jobInfo, List<JobExampleEntity> jobExampleList) {
        List<JobFileEntity> jobFileEntityList = new ArrayList<>();
        IntStream.range(0, jobExampleList.size()).forEach(t -> {
            JobExampleEntity jobExample = jobExampleList.get(t);
            // 脚本文件
            jobFileEntityList.add(new JobFileEntity()
                    .setJobId(jobId)
                    .setEndPoint(-1L)
                    .setStartPoint(-1L)
                    .setType(FileType.SCRIPT.getCode())
                    .setJobExampleId(jobExample.getId())
                    .setUri(jobInfo.getScriptFile().getUri()));
            // 数据文件
            List<FileInfo> dataFile = jobInfo.getDataFile() == null ? new ArrayList<>() : jobInfo.getDataFile();
            dataFile.stream().map(c -> {
                JobFileEntity jobFileEntity = new JobFileEntity()
                        .setJobId(jobId)
                        .setUri(c.getUri())
                        .setType(FileType.DATA.getCode())
                        .setJobExampleId(jobExample.getId())
                        .setEndPoint(-1L)
                        .setStartPoint(-1L);
                if (Objects.nonNull(c.getSplitList())) {
                    jobFileEntity.setEndPoint(c.getSplitList().get(t).getEnd())
                            .setStartPoint(c.getSplitList().get(t).getStart());
                }
                String name = FileUtil.getName(c.getUri());
                if(StringUtils.indexOf(name, "jar") != -1){
                    jobFileEntity.setType(FileType.PLUGIN.getCode());
                }
                return jobFileEntity;
            }).forEach(jobFileEntityList::add);
            // 依赖文件
            List<FileInfo> dependencyFile = jobInfo.getDependencyFile() == null ? new ArrayList<>(0) : jobInfo.getDependencyFile();
            dependencyFile.stream().map(c -> (new JobFileEntity()
                    .setJobId(jobId)
                    .setEndPoint(-1L)
                    .setUri(c.getUri())
                    .setStartPoint(-1L)
                    .setJobExampleId(jobExample.getId()))
                    .setType(FileType.ATTACHMENT.getCode())).forEach(jobFileEntityList::add);
        });
        return jobFileEntityList;
    }

    /**
     * 填充指标信息
     *
     * @param jobId           任务主键
     * @param metricsInfoList 指标信息
     * @return 指标信息
     */
    private List<MetricsEntity> startFillMetrics(long jobId, List<MetricsInfo> metricsInfoList) {
        return metricsInfoList.stream().map(t -> {
            String context = null;
            try {
                Map<String, Object> contextObject = new HashMap<>(4);
                contextObject.put("sa", t.getSa());
                contextObject.put("rt", t.getRt());
                contextObject.put("tps", t.getTps());
                contextObject.put("successRate", t.getSuccessRate());
                context = jsonService.writeValueAsString(contextObject);
            } catch (Exception e) {
                log.warn("JSON序列化失败");
            }
            return new MetricsEntity().setJobId(jobId).setContext(context).setRef(t.getRef());
        }).collect(Collectors.toList());

    }

    /**
     * 转换线程配置信息
     *
     * @param threadConfigInfo 线程配置信息
     * @return 转换后的Map
     */
    private Map<String, Object> threadConfigInfo(ThreadConfigInfo threadConfigInfo) {
        Map<String, Object> content = new HashMap<>(5);
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
                context = jsonService.readValue(t.getContext(), new TypeReference<ThreadConfigInfo>() {
                });
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
        List<ThreadConfigExampleEntity> threadConfigExample = jobConfigService.threadExampleItem(jobId, context.getRef());
        // 2.1 如果没有抛出异常
        if (CollUtil.isEmpty(threadConfigExample)) {
            throw new IllegalArgumentException("未找到可修改的配置");
        }
        // 2.2 存在即修改
        else {
            List<List<ThreadConfigInfo>> splitThreadConfig = splitThreadConfig(CollUtil.toList(context.getContext()), threadConfigExample.size());
            List<ThreadConfigInfo> splitThreadConfigResult = splitThreadConfig.get(0);
            IntStream.range(0, splitThreadConfigResult.size()).forEach(t -> {
                long threadConfigExampleId = threadConfigExample.get(t).getId();
                String contextString = jsonService.writeValueAsString(splitThreadConfigResult.get(t));
                // 2.2.1 更新任务配置实例项
                jobConfigService.modifThreadConfigExample(threadConfigExampleId, context.getType(), contextString);
            });
        }
        // 2.3 下发命令
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
        threadConfigInfoList.forEach(t -> {
            List<Integer> tpsList = splitInteger(t.getTps() == null ? 0 : t.getTps(), size);
            List<Integer> numberList = splitInteger(t.getNumber() == null ? 0 : t.getNumber(), size);
            result.add(IntStream.range(0, size).mapToObj(c -> new ThreadConfigInfo()
                    .setRef(t.getRef())
                    .setType(t.getType())
                    .setTps(tpsList.get(c))
                    .setDuration(t.getDuration())
                    .setNumber(numberList.get(c))
                    .setGrowthStep(t.getGrowthStep())
                    .setGrowthTime(t.getGrowthTime())).collect(Collectors.toList()));
        });
        return result;
    }

    /**
     * 切分数值
     * <p>余数平分到每一项</p>
     * <p>如果要切分小数，可以转换为指定精度的int</p>
     *
     * @param value 需要分隔的值
     * @param size  分隔的份数
     * @return 结果集合
     */
    private List<Integer> splitInteger(int value, int size) {
        int quotient = value / size;
        int remainder = value % size;
        if (quotient == 0 && remainder != 0) {
            throw new NumberFormatException(CharSequenceUtil.format("无法把{}分隔成{}份", value, size));
        }
        return IntStream.range(0, size).mapToObj(t -> {
            // 基数为商
            int tempValue = quotient;
            // 附加余数
            if (remainder > t) {
                tempValue++;
            }
            // 返回单片结果
            return tempValue;
        }).collect(Collectors.toList());
    }
}
