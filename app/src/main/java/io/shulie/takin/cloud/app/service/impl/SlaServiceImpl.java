package io.shulie.takin.cloud.app.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.model.callback.Sla;
import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.app.mapper.SlaMapper;
import io.shulie.takin.cloud.app.entity.SlaEntity;
import io.shulie.takin.cloud.app.service.JobService;
import io.shulie.takin.cloud.app.service.SlaService;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.entity.SlaEventEntity;
import io.shulie.takin.cloud.model.callback.Sla.SlaInfo;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.constant.enums.FormulaSymbol;
import io.shulie.takin.cloud.constant.enums.FormulaTarget;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.model.notify.Metrics.MetricsInfo;
import io.shulie.takin.cloud.app.service.mapper.SlaEventMapperService;

/**
 * SLA服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class SlaServiceImpl implements SlaService {
    @javax.annotation.Resource
    SlaMapper slaMapper;
    @javax.annotation.Resource
    JobService jobService;
    @javax.annotation.Resource
    JsonService jsonService;
    @javax.annotation.Resource
    ResourceService resourceService;
    @javax.annotation.Resource
    CallbackService callbackService;
    @javax.annotation.Resource
    SlaEventMapperService slaEventMapperService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SlaEntity> list(long jobId) {
        return slaMapper.selectList(new LambdaQueryWrapper<SlaEntity>().eq(SlaEntity::getJobId, jobId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(long jobId, String ref, FormulaTarget target, FormulaSymbol symbol, double number) {
        slaMapper.insert(new SlaEntity() {{
            setRef(ref);
            setJobId(jobId);
            setFormulaNumber(number);
            setFormulaTarget(target.getCode());
            setFormulaSymbol(symbol.getCode());
        }});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void event(Long jobId, Long jobExampleId, List<SlaEventEntity> slaEventEntityList) {
        if (CollUtil.isEmpty(slaEventEntityList)) {return;}
        slaEventMapperService.saveBatch(slaEventEntityList);
        {
            JobEntity jobEntity = jobService.jobEntity(jobId);
            JobExampleEntity jobExampleEntity = jobService.jobExampleEntity(jobExampleId);
            ResourceExampleEntity resourceExampleEntity = resourceService.exampleEntity(jobExampleEntity.getResourceExampleId());
            List<SlaInfo> slaInfoList = slaEventEntityList.stream().map(t -> new SlaInfo() {{
                setJobId(jobId);
                setRef(t.getRef());
                setNumber(t.getNumber());
                setAttach(t.getAttach());
                setJobExampleId(jobExampleId);
                setFormulaNumber(t.getFormulaNumber());
                setFormulaSymbol(t.getFormulaSymbol());
                setFormulaTarget(t.getFormulaTarget());
                setResourceExampleId(resourceExampleEntity.getId());
                setResourceId(resourceExampleEntity.getResourceId());
            }}).collect(Collectors.toList());
            Sla sla = new Sla() {{
                setTime(new Date());
                setData(slaInfoList);
                setCallbackTime(getTime());
            }};
            String slaString = jsonService.writeValueAsString(sla);
            callbackService.create(jobEntity.getCallbackUrl(), StrUtil.utf8Bytes(slaString));
        }
    }

    @Override
    public List<SlaEventEntity> check(Long jobId, Long jobExampleId, List<MetricsInfo> metricsInfoList) {
        // 业务结果
        List<SlaEventEntity> result = new ArrayList<>();
        // 获取条件
        List<SlaEntity> slaEntityList = slaMapper.selectList(new LambdaQueryWrapper<SlaEntity>()
            .eq(SlaEntity::getJobId, jobId));
        // 逐个数据判断
        for (int i = 0; i < metricsInfoList.size(); i++) {
            MetricsInfo metricsInfo = metricsInfoList.get(i);
            // 对应的条件列表
            List<SlaEntity> conditionList = slaEntityList.stream()
                .filter(t -> t.getRef().equals(metricsInfo.getTransaction())).collect(Collectors.toList());
            // 逐个条件判断
            for (int j = 0; j < conditionList.size(); j++) {
                SlaEntity condition = conditionList.get(j);
                FormulaSymbol formulaSymbol = FormulaSymbol.of(condition.getFormulaSymbol());
                FormulaTarget formulaTarget = FormulaTarget.of(condition.getFormulaTarget());
                Double compareResult = compare(metricsInfo, formulaTarget, formulaSymbol, condition.getFormulaNumber());
                // 符合校验则添加到业务结果里面
                if (compareResult != null) {
                    result.add(new SlaEventEntity() {{
                        setJobId(jobId);
                        setNumber(compareResult);
                        setRef(condition.getRef());
                        setSlaId(condition.getId());
                        setJobExampleId(jobExampleId);
                        setAttach(condition.getAttach());
                        setFormulaNumber(condition.getFormulaNumber());
                        setFormulaTarget(condition.getFormulaTarget());
                        setFormulaSymbol(condition.getFormulaSymbol());
                    }});
                }
            }
        }
        return result;
    }

    /**
     * 算式是否成立
     *
     * @param info   数据源信息
     * @param target 算式目标
     * @param symbol 算式符号
     * @param value  比较值
     * @return 成立则返回算式目标的实际值<br />
     * 否则返回null
     */
    Double compare(MetricsInfo info, FormulaTarget target, FormulaSymbol symbol, double value) {
        Double targetValue = null;
        switch (target) {
            case RT:
                targetValue = info.getRt();
                break;
            case SA:
                targetValue = (info.getSaCount() * 1.0) / info.getCount();
                break;
            case TPS:
                targetValue = (info.getCount() * 1.0) / 5;
                break;
            case SUCCESS_RATE:
                targetValue = 1 - ((info.getFailCount() * 1.0) / info.getCount());
                break;
            default:
                return targetValue;
        }
        // 进行数值比较
        int compareResult = targetValue.compareTo(value);
        // 翻转比较值
        compareResult = compareResult * -1;
        switch (symbol) {
            case EQUAL:
                targetValue = compareResult == 0 ? targetValue : null;
                break;
            case GREATER_THAN:
                targetValue = compareResult > 0 ? targetValue : null;
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                targetValue = compareResult >= 0 ? targetValue : null;
                break;
            case LESS_THAN:
                targetValue = compareResult < 0 ? targetValue : null;
                break;
            case LESS_THAN_OR_EQUAL_TO:
                targetValue = compareResult <= 0 ? targetValue : null;
                break;
            default:
                return targetValue;
        }
        return targetValue;
    }
}
