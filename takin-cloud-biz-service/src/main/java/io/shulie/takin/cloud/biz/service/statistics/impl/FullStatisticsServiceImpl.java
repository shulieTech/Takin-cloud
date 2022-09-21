package io.shulie.takin.cloud.biz.service.statistics.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.math.RoundingMode;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.biz.service.statistics.FullStatisticsService;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.sdk.model.response.statistics.FullResponse;
import io.shulie.takin.cloud.sdk.model.response.statistics.FullResponse.TopItem;

/**
 * 全量统计
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class FullStatisticsServiceImpl implements FullStatisticsService {

    @javax.annotation.Resource
    private SceneManageMapper sceneManageMapper;
    @javax.annotation.Resource
    private ReportMapper reportMapper;
    @Value("${statistics.display.full:false}")
    private Boolean statisticsDisplayFull;

    private static final String COUNT_COLUMN = "group_count";
    private static final String COUNT_SELECT_COLUMN = "COUNT(1)AS`" + COUNT_COLUMN + "`";

    /**
     * {@inheritDoc}
     */
    @Override
    public FullResponse full(Date startTime, Date endTime, Integer topNumber) {
        // 场景信息
        FullResponse.Scene sceneResponse = fullScene(startTime, endTime);
        // 报告信息
        FullResponse.Report reportResponse = fullReport(startTime, endTime);
        // Top榜单
        List<TopItem> topItems = topList(topNumber, startTime, endTime);
        return new FullResponse().setScene(sceneResponse).setReport(reportResponse).setTopList(topItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullResponse.Scene fullScene(Date startTime, Date endTime) {
        FullResponse.Scene result = new FullResponse.Scene();
        String statusColumn = "status";
        // 统计场景
        QueryWrapper<SceneManageEntity> sceneWrapper = new QueryWrapper<SceneManageEntity>()
            .select(statusColumn, COUNT_SELECT_COLUMN)
            .eq("tenant_id", CloudPluginUtils.getTenantId())
            .eq("env_code", CloudPluginUtils.getEnvCode())
            .eq("is_deleted", false)
            .ge("create_time", startTime)
            .le("create_time", endTime)
            .eq("type", 0)
            .groupBy(statusColumn);
        Map<Integer, Integer> sceneInfo = new HashMap<>(16);
        sceneManageMapper.selectMaps(sceneWrapper).forEach(t -> {
            Object key = t.get(statusColumn);
            Integer status = ObjectUtil.isNull(key) || CharSequenceUtil.isBlank(key.toString()) ?
                null : NumberUtil.parseInt(key.toString());
            int count = NumberUtil.parseInt(t.get(COUNT_COLUMN).toString());
            sceneInfo.put(status, count);
        });
        int sceneCount = 0;
        List<Integer> freeStatusList = SceneManageStatusEnum.getFree()
            .stream().map(SceneManageStatusEnum::getValue)
            .collect(Collectors.toList());
        List<Integer> runningStatusList = SceneManageStatusEnum.getWorking()
            .stream().map(SceneManageStatusEnum::getValue)
            .collect(Collectors.toList());
        for (Entry<Integer, Integer> entry : sceneInfo.entrySet()) {
            Integer k = entry.getKey();
            Integer v = entry.getValue();
            if (freeStatusList.contains(k)) {
                result.setRunableCount(result.getRunableCount() + v);
            } else if (runningStatusList.contains(k)) {
                result.setRunningCount(result.getRunningCount() + v);
            } else if (Boolean.TRUE.equals(statisticsDisplayFull)) {
                // 界面上需要数值匹配的话，就忽略掉异常项
                continue;
            }
            sceneCount += v;
        }
        // 场景的比例计算(保留两位小数，四舍五入)
        result.setCount(sceneCount);
        result.setRunableProportion(calcProportion(result.getRunningCount(), result.getCount()));
        result.setRunningProportion(calcProportion(result.getRunableCount(), result.getCount()));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullResponse.Report fullReport(Date startTime, Date endTime) {
        FullResponse.Report result = new FullResponse.Report();
        String conclusionColumn = "conclusion";
        // 统计报告
        int reportCount = 0;
        QueryWrapper<ReportEntity> reportWrapper = getReportWrapper(startTime, endTime, conclusionColumn, COUNT_SELECT_COLUMN)
            .groupBy(conclusionColumn);
        Map<Boolean, Integer> reportInfo = new HashMap<>(16);
        reportMapper.selectMaps(reportWrapper).forEach(t -> {
            Object key = t.get(conclusionColumn);
            Boolean conclusion = ObjectUtil.isNull(key) || CharSequenceUtil.isBlank(key.toString()) ?
                null : BooleanUtil.toBoolean(key.toString());
            int number = NumberUtil.parseInt(t.get(COUNT_COLUMN).toString());
            reportInfo.put(conclusion, number);
        });
        for (Entry<Boolean, Integer> entry : reportInfo.entrySet()) {
            Boolean k = entry.getKey() == null ? null : entry.getKey();
            Integer v = entry.getValue();
            if (Boolean.TRUE.equals(k)) {
                result.setConclusionTrueCount(result.getConclusionTrueCount() + v);
            } else if (Boolean.FALSE.equals(k)) {
                result.setConclusionFalseCount(result.getConclusionFalseCount() + v);
            } else if (Boolean.TRUE.equals(statisticsDisplayFull)) {
                // 界面上需要数值匹配的话，就忽略掉异常项
                continue;
            }
            reportCount += v;
        }
        // 报告的比例计算(保留两位小数，四舍五入)
        result.setCount(reportCount);
        result.setConclusionTrueProportion(calcProportion(result.getConclusionTrueCount(), result.getCount()));
        result.setConclusionFalseProportion(calcProportion(result.getConclusionFalseCount(), result.getCount()));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullResponse.TopItem> topList(int number, Date startTime, Date endTime) {
        // 虚拟标识数量
        int virtualCount = 0;
        // 真实结果集合
        List<FullResponse.TopItem> result = new ArrayList<>(number);
        // 是否进入重排序阶段
        boolean reRanking = false;
        // 数据抽取阶段
        while (!reRanking) {
            // 偏移量 = 真实结果的集合数量 + 虚拟标识的数量
            int offset = result.size() + virtualCount;
            // 抽取一次数据
            List<FullResponse.TopItem> onceResult = topListOnce(offset, number, startTime, endTime);
            // 没有数据了
            if (CollUtil.isEmpty(onceResult)) {reRanking = true;}
            // 单次数据抽取完成
            else {
                // 单次抽取的真实结果集合
                List<FullResponse.TopItem> actualItems = onceResult.stream()
                    .filter(t -> t.getSceneId() != null).collect(Collectors.toList());
                // 填充真实结果
                result.addAll(actualItems);
                // 叠加虚拟标识数量
                virtualCount += (onceResult.size() - actualItems.size());
            }
            // 判断是否需要继续抽取数据
            // 1. 真实结果的长度大于需要的数量
            if (result.size() > number) {
                // 理论上的压测总数
                int theoreticalReportCount = result.get(number - 1).getReportCount();
                // 真实的最后一个数据的压测总数
                int lastReportCount = result.get(result.size() - 1).getReportCount();
                // 2. 必须冗余抽取，在排序值不一致的情况下，才能保证多个（两个）字段进行排序的正确性
                if (!Objects.equals(theoreticalReportCount, lastReportCount)) {reRanking = true;}
            }
        }
        // 重排序
        result = result.stream()
            .sorted((o2, o1) -> {
                int reportCountCompareResult = Integer.compare(o1.getReportCount(), o2.getReportCount());
                // 直接返回对比结果
                if (reportCountCompareResult != 0) {return reportCountCompareResult;}
                // 利用第二个字段再次对比
                else {return Long.compare(o1.getLastTime(), o2.getLastTime());}
            })
            .limit(number)
            .collect(Collectors.toList());
        // 返回结果
        return result;
    }

    /**
     * 抽取数据 - 单次
     *
     * @param offset    偏移量
     * @param limit     限定数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总是返回和<code>limit</code>相同长度的集合,或者是长度为0的集合
     */
    private List<FullResponse.TopItem> topListOnce(int offset, int limit, Date startTime, Date endTime) {
        String sceneIdColumn = "scene_id";
        try (Page<Object> ignore = PageMethod.offsetPage(offset, limit)) {
            // 场景主键,压测次数
            Map<Long, Integer> countMap = new HashMap<>(limit);
            // 根据scene_id聚合后排序
            QueryWrapper<ReportEntity> reportWrapper = getReportWrapper(startTime, endTime,
                sceneIdColumn, COUNT_SELECT_COLUMN)
                .groupBy(sceneIdColumn)
                .orderByDesc(COUNT_COLUMN);
            reportMapper.selectMaps(reportWrapper)
                .forEach(t -> {
                    Object key = t.get(sceneIdColumn);
                    Long sceneId = ObjectUtil.isNull(key) || CharSequenceUtil.isBlank(key.toString()) ?
                        null : NumberUtil.parseLong(key.toString());
                    int number = NumberUtil.parseInt(t.get(COUNT_COLUMN).toString());
                    // 未压测过的不参与
                    if (number > 0) {countMap.put(sceneId, number);}
                    // 保证待选项不为空
                    else {countMap.put(null, number);}
                });
            if (CollUtil.isEmpty(countMap)) {return new ArrayList<>(0);}
            // 查场景详情
            LambdaQueryWrapper<SceneManageEntity> sceneWrapper = new LambdaQueryWrapper<SceneManageEntity>()
                .select(SceneManageEntity::getId, SceneManageEntity::getSceneName,
                    SceneManageEntity::getCreateTime, SceneManageEntity::getUserId)
                .eq(SceneManageEntity::getType, 0)
                .in(SceneManageEntity::getId, countMap.keySet())
                .eq(SceneManageEntity::getEnvCode, CloudPluginUtils.getEnvCode())
                .eq(SceneManageEntity::getTenantId, CloudPluginUtils.getTenantId());
            List<SceneManageEntity> entities = sceneManageMapper.selectList(sceneWrapper);
            // 场景信息进行数据转换
            List<FullResponse.TopItem> result = entities.stream()
                .map(t -> new FullResponse.TopItem()
                    .setSceneId(t.getId())
                    .setSceneName(t.getSceneName())
                    .setSceneCreateUserId(t.getUserId())
                    .setReportCount(countMap.get(t.getId()))
                    .setSceneCreateTime(t.getCreateTime().getTime())
                )
                .map(t -> fullSceneReportInfo(t, startTime, endTime))
                .collect(Collectors.toList());
            // 填充标识节点
            for (int i = 0, size = result.size(); i < limit - size; i++) {
                result.add(new FullResponse.TopItem().setSceneId(null));
            }
            return result;
        }

    }

    private String calcProportion(int number, int base) {
        if (base == 0) {return "0.00";}
        return NumberUtil.div(StrUtil.toString(number), StrUtil.toString(base), 2, RoundingMode.HALF_UP).toString();
    }

    private QueryWrapper<ReportEntity> getReportWrapper(Date startTime, Date endTime, String... columns) {
        return new QueryWrapper<ReportEntity>()
            .select(columns)
            .eq("type", 0)
            .eq("status", 2)
            .eq("tenant_id", CloudPluginUtils.getTenantId())
            .eq("env_code", CloudPluginUtils.getEnvCode())
            .ge("gmt_create", startTime)
            .lt("gmt_create", endTime)
            .eq("is_deleted", false);
    }

    /**
     * 填充场景的报告信息
     *
     * @param item      场景信息
     * @param startTime 启动时间
     * @param endTime   结束时间
     * @return 补充了 结论为通过的报告数量、结论为不通过的报告数量、最新压测时间 后的对象
     */
    private FullResponse.TopItem fullSceneReportInfo(FullResponse.TopItem item, Date startTime, Date endTime) {
        String conclusionColumn = "conclusion";
        FullResponse.TopItem result = new FullResponse.TopItem()
            .setSceneId(item.getSceneId())
            .setSceneName(item.getSceneName())
            .setReportCount(item.getReportCount())
            .setSceneCreateTime(item.getSceneCreateTime())
            .setSceneCreateUserId(item.getSceneCreateUserId());
        QueryWrapper<ReportEntity> reportWrapper = getReportWrapper(startTime, endTime,
            conclusionColumn, COUNT_SELECT_COLUMN, "UNIX_TIMESTAMP(MAX(gmt_create))AS`time`")
            .eq("scene_id", result.getSceneId()).groupBy(conclusionColumn);
        reportMapper.selectMaps(reportWrapper).forEach(t -> {
            Object key = t.get(conclusionColumn);
            Boolean conclusion = ObjectUtil.isNull(key) || CharSequenceUtil.isBlank(key.toString()) ?
                null : BooleanUtil.toBoolean(key.toString());
            int number = NumberUtil.parseInt(t.get(COUNT_COLUMN).toString());
            long time = NumberUtil.parseLong(t.get("time").toString()) * 1000L;
            // 结论为通过的报告数量
            if (Boolean.TRUE.equals(conclusion)) {result.setReportConclusionTrueCount(number);}
            // 结论为不通过的报告数量
            else if (Boolean.FALSE.equals(conclusion)) {result.setReportConclusionFalseCount(number);}
            result.setLastTime(Math.max(result.getLastTime(), time));
        });
        return result;
    }
}
