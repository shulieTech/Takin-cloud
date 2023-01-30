package io.shulie.takin.cloud.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.data.entity.SlaEntity;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.constant.enums.FileType;
import io.shulie.takin.cloud.data.entity.PressureEntity;
import io.shulie.takin.cloud.app.service.PressureService;
import io.shulie.takin.cloud.data.entity.MetricsEntity;
import io.shulie.takin.cloud.data.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.model.response.PressureConfig;
import io.shulie.takin.cloud.data.entity.PressureFileEntity;
import io.shulie.takin.cloud.data.service.SlaMapperService;
import io.shulie.takin.cloud.data.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.data.entity.PressureExampleEntity;
import io.shulie.takin.cloud.app.service.PressureConfigService;
import io.shulie.takin.cloud.data.service.MetricsMapperService;
import io.shulie.takin.cloud.data.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.service.PressureExampleService;
import io.shulie.takin.cloud.data.service.PressureMapperService;
import io.shulie.takin.cloud.app.service.ResourceExampleService;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.SlaInfo;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.FileInfo;
import io.shulie.takin.cloud.data.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.data.service.PressureFileMapperService;
import io.shulie.takin.cloud.data.service.ThreadConfigMapperService;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.MetricsInfo;
import io.shulie.takin.cloud.model.request.job.pressure.ModifyConfig;
import io.shulie.takin.cloud.data.service.PressureExampleMapperService;
import io.shulie.takin.cloud.model.request.job.pressure.StartRequest.ThreadConfigInfo;
import io.shulie.takin.cloud.data.service.ThreadConfigExampleMapperService;

/**
 * 施压任务服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class PressureServiceImpl implements PressureService {
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    CommandService commandService;
    @javax.annotation.Resource
    ResourceService resourceService;
    @javax.annotation.Resource
    PressureConfigService pressureConfigService;
    @javax.annotation.Resource
    PressureExampleService pressureExampleService;
    @javax.annotation.Resource(name = "slaMapperServiceImpl")
    SlaMapperService slaMapper;
    @javax.annotation.Resource(name = "metricsMapperServiceImpl")
    MetricsMapperService metricsMapper;
    @javax.annotation.Resource(name = "pressureMapperServiceImpl")
    PressureMapperService pressureMapper;
    @javax.annotation.Resource(name = "resourceExampleServiceImpl")
    ResourceExampleService resourceExample;
    @javax.annotation.Resource(name = "pressureFileMapperServiceImpl")
    PressureFileMapperService pressureFileMapper;
    @javax.annotation.Resource(name = "threadConfigMapperServiceImpl")
    ThreadConfigMapperService threadConfigMapper;
    @javax.annotation.Resource(name = "pressureExampleMapperServiceImpl")
    PressureExampleMapperService pressureExampleMapper;
    @javax.annotation.Resource(name = "threadConfigExampleMapperServiceImpl")
    ThreadConfigExampleMapperService threadConfigExampleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public String start(StartRequest info) {
        // 获取资源
        ResourceEntity resource = resourceService.entity(info.getResourceId());
        // 生成任务
        PressureEntity pressure = fillPressure(resource.getId(), resource.getNumber(), info);
        pressureMapper.save(pressure);
        // 填充施压任务实例
        List<PressureExampleEntity> pressureExample = fillPressureExample(pressure.getId(), pressure.getDuration(), resource.getId(), resource.getNumber());
        pressureExampleMapper.saveBatch(pressureExample);
        // 填充线程组配置
        List<ThreadConfigEntity> threadConfig = startFillThreadConfig(pressure.getId(), info);
        threadConfigMapper.saveBatch(threadConfig);
        // 填充线程配置实例
        List<ThreadConfigExampleEntity> threadConfigExample = startFillThreadConfigExample(pressure.getId(), info, pressureExample);
        threadConfigExampleMapper.saveBatch(threadConfigExample);
        // 填充SLA配置
//        List<SlaEntity> slaList = startFillSla(pressure.getId(), info.getSlaConfig());
//        slaMapper.saveBatch(slaList);
        // 切分、填充任务文件
        List<PressureFileEntity> fileList = fillFile(pressure.getId(), info, pressureExample);
        pressureFileMapper.saveBatch(fileList);
        // 指标目标
        List<MetricsEntity> metricsList = startFillMetrics(pressure.getId(), info.getMetricsConfig());
        metricsMapper.saveBatch(metricsList);
        // 下发启动命令
        commandService.startApplication(pressure.getId(), info);
        // 返回任务主键
        return String.valueOf(pressure.getId());
    }

    /**
     * 填充任务实体
     *
     * @param info                  施压任务信息
     * @param resourceId            资源主键
     * @param resourceExampleNumber 资源实例数量
     * @return 施压任务实体
     */
    private PressureEntity fillPressure(long resourceId, int resourceExampleNumber, StartRequest info) {
        // 时长取最大值
        Integer duration = info.getThreadConfig()
            .stream().map(ThreadConfigInfo::getDuration)
            .max(Comparator.naturalOrder()).orElse(0);
        return new PressureEntity()
            .setResourceId(resourceId)
            .setName(info.getName())
            .setDuration(duration)
            .setSampling(info.getSampling())
            .setType(info.getType().getCode())
            .setStartOption(info.getJvmOptions())
            .setCallbackUrl(info.getCallbackUrl())
            .setResourceExampleNumber(resourceExampleNumber);
    }

    /**
     * 填充施压任务实例
     *
     * @param pressureId     施压任务主键
     * @param duration       持续时长
     * @param resourceId     资源主键
     * @param resourceNumber 资源需要生成的实例数量
     * @return 施压任务实例
     */
    private List<PressureExampleEntity> fillPressureExample(long pressureId, int duration, long resourceId, int resourceNumber) {
        List<ResourceExampleEntity> resourceExampleEntityList = resourceService.listExample(resourceId);
        return IntStream.range(0, resourceNumber).mapToObj(t -> new PressureExampleEntity()
            .setPressureId(pressureId)
            .setNumber(t + 1)
            .setDuration(duration)
            .setResourceExampleId(resourceExampleEntityList.get(t).getId())).collect(Collectors.toList());
    }

    /**
     * 填充线程组配置
     *
     * @param info       施压任务信息
     * @param pressureId 施压任务主键
     * @return 线程组配置
     */
    private List<ThreadConfigEntity> startFillThreadConfig(long pressureId, StartRequest info) {
        return info.getThreadConfig().stream().map(threadConfigInfo -> {
            Map<String, Object> context = threadConfigInfo(threadConfigInfo);
            if (info.getExt() != null) {
                context.putAll(info.getExt());
            }
            return new ThreadConfigEntity()
                .setPressureId(pressureId)
                .setRef(threadConfigInfo.getRef())
                .setMode(threadConfigInfo.getType().getCode())
                .setContext(jsonService.writeValueAsString(context));
        }).collect(Collectors.toList());
    }

    /**
     * 启动任务 - 填充线程配置实例
     *
     * @param pressureId      施压任务主键
     * @param info            施压任务信息
     * @param pressureExample 施压任务实例实体
     * @return 线程配置实例
     */
    private List<ThreadConfigExampleEntity> startFillThreadConfigExample(long pressureId, StartRequest info, List<PressureExampleEntity> pressureExample) {
        // 切分线程配置
        List<List<ThreadConfigInfo>> splitResult = splitThreadConfig(info.getThreadConfig(), pressureExample.size());
        // 组装返回值
        List<ThreadConfigExampleEntity> threadConfigExample = new ArrayList<>(pressureExample.size());
        PressureExampleEntity pressureExampleEntity = pressureExample.get(0);
        IntStream.range(0, splitResult.size()).forEach(t -> {
            List<ThreadConfigInfo> threadConfigInfoList = splitResult.get(t);
            IntStream.range(0, threadConfigInfoList.size()).mapToObj(c -> {
                ThreadConfigInfo z = threadConfigInfoList.get(c);
                Map<String, Object> context = threadConfigInfo(z);
                if (info.getExt() != null) {
                    context.putAll(info.getExt());
                }
                return new ThreadConfigExampleEntity()
                    .setPressureId(pressureId)
                    .setRef(z.getRef())
                    .setSerialNumber(c)
                    .setType(z.getType().getCode())
                    .setPressureExampleId(pressureExampleEntity.getId())
                    .setContext(jsonService.writeValueAsString(context));
            }).forEach(threadConfigExample::add);
        });
        return threadConfigExample;
    }

    /**
     * 填充SLA
     *
     * @param pressureId  施压任务主键
     * @param slaInfoList SLA信息
     * @return SLA
     */
    private List<SlaEntity> startFillSla(long pressureId, List<SlaInfo> slaInfoList) {
        return slaInfoList.stream().map(t -> new SlaEntity()
            .setPressureId(pressureId)
            .setRef(t.getRef())
            .setAttach(t.getAttach())
            .setFormulaNumber(t.getFormulaNumber())
            .setFormulaTarget(t.getFormulaTarget().getCode())
            .setFormulaSymbol(t.getFormulaSymbol().getCode())).collect(Collectors.toList());
    }

    /**
     * 填充施压任务文件
     *
     * @param pressureId          施压任务主键
     * @param pressureInfo        施压任务信息
     * @param pressureExampleList 施压任务实例实体
     * @return 施压任务文件
     */
    private List<PressureFileEntity> fillFile(long pressureId, StartRequest pressureInfo, List<PressureExampleEntity> pressureExampleList) {
        List<PressureFileEntity> pressureFileEntityList = new ArrayList<>();
        IntStream.range(0, pressureExampleList.size()).forEach(t -> {
            PressureExampleEntity pressureExample = pressureExampleList.get(t);
            // 脚本文件
            pressureFileEntityList.add(new PressureFileEntity()
                .setPressureId(pressureId)
                .setEndPoint(-1L)
                .setStartPoint(-1L)
                .setType(FileType.SCRIPT.getCode())
                .setPressureExampleId(pressureExample.getId())
                .setUri(pressureInfo.getScriptFile().getUri()));
            // 数据文件
            List<FileInfo> dataFile = pressureInfo.getDataFile() == null ? new ArrayList<>() : pressureInfo.getDataFile();
            dataFile.stream().map(c -> {
                PressureFileEntity pressureFileEntity = new PressureFileEntity()
                    .setPressureId(pressureId)
                    .setUri(c.getUri())
                    .setType(FileType.DATA.getCode())
                    .setPressureExampleId(pressureExample.getId())
                    .setEndPoint(-1L)
                    .setStartPoint(-1L);
                if (Objects.nonNull(c.getSplitList())) {
                    pressureFileEntity.setEndPoint(c.getSplitList().get(t).getEnd())
                        .setStartPoint(c.getSplitList().get(t).getStart());
                }
                String name = FileUtil.getName(c.getUri());
                if (CharSequenceUtil.indexOfIgnoreCase(name, "jar") != -1) {
                    pressureFileEntity.setType(FileType.PLUGIN.getCode());
                }
                return pressureFileEntity;
            }).forEach(pressureFileEntityList::add);
            // 依赖文件
            List<FileInfo> dependencyFile = pressureInfo.getDependencyFile() == null ? new ArrayList<>(0) : pressureInfo.getDependencyFile();
            dependencyFile.stream().map(c -> (new PressureFileEntity()
                .setPressureId(pressureId)
                .setEndPoint(-1L)
                .setUri(c.getUri())
                .setStartPoint(-1L)
                .setPressureExampleId(pressureExample.getId()))
                .setType(FileType.ATTACHMENT.getCode())).forEach(pressureFileEntityList::add);
        });
        return pressureFileEntityList;
    }

    /**
     * 填充指标信息
     *
     * @param pressureId      施压任务主键
     * @param metricsInfoList 指标信息
     * @return 指标信息
     */
    private List<MetricsEntity> startFillMetrics(long pressureId, List<MetricsInfo> metricsInfoList) {
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
            return new MetricsEntity().setPressureId(pressureId).setContext(context).setRef(t.getRef());
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
    public void stop(Long pressureId) {
        // 获取任务
        PressureEntity pressureEntity = pressureMapper.getById(pressureId);
        if (pressureEntity == null) {
            throw new IllegalArgumentException(CharSequenceUtil.format(Message.MISS_PRESSURE, pressureId));
        }
        // 停止任务
        commandService.stopApplication(pressureEntity.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PressureConfig> getConfig(Long id, String ref) {
        List<ThreadConfigExampleEntity> threadConfigExampleEntity = pressureConfigService.threadExampleItem(id, ref);
        return threadConfigExampleEntity.stream().map(t -> {
            ThreadConfigInfo context = null;
            try {
                context = jsonService.readValue(t.getContext(), ThreadConfigInfo.class);
            } catch (RuntimeException e) {
                log.warn("线程组配置实例context解析失败");
            }
            ThreadConfigInfo finalContext = context;
            return new PressureConfig()
                .setRef(t.getRef())
                .setContext(finalContext)
                .setPressureId(t.getPressureId())
                .setType(ThreadGroupType.of(t.getType()));
        }).collect(Collectors.toList());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyConfig(Long id, ModifyConfig context) {
        // 1. 找到要修改的配置项
        List<ThreadConfigExampleEntity> threadConfigExample = pressureConfigService.threadExampleItem(id, context.getRef());
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
                pressureConfigService.modifThreadConfigExample(threadConfigExampleId, context.getType(), contextString);
            });
        }
        // 2.3 下发命令
        commandService.updateConfig(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PressureEntity entity(Long id) {
        return pressureMapper.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PressureExampleEntity exampleEntity(Long exampleId) {
        return pressureExampleMapper.getById(exampleId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PressureExampleEntity> exampleEntityList(Long pressureId) {
        return pressureExampleMapper.lambdaQuery()
            .eq(PressureExampleEntity::getPressureId, pressureId)
            .list();
    }

    @Override
    public void onStart(Long pressureId) {
        exampleEntityList(pressureId).forEach(t -> pressureExampleService.onStart(t.getId()));
    }

    @Override
    public void onStop(Long pressureId) {
        exampleEntityList(pressureId).forEach(t -> {
            // 停止任务实例
            pressureExampleService.onStop(t.getId());
            // 停止任务实例对应的资源实例
            resourceExample.onStop(t.getResourceExampleId());
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
