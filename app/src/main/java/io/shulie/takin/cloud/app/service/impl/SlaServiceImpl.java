package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.app.service.mapper.SlaEventMapperService;
import io.shulie.takin.cloud.model.notify.Metrics.MetricsInfo;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.mapper.SlaMapper;
import io.shulie.takin.cloud.app.entity.SlaEntity;
import io.shulie.takin.cloud.app.service.SlaService;
import io.shulie.takin.cloud.app.entity.SlaEventEntity;
import io.shulie.takin.cloud.app.mapper.SlaEventMapper;
import io.shulie.takin.cloud.constant.enums.FormulaSymbol;
import io.shulie.takin.cloud.constant.enums.FormulaTarget;

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
    public void event(List<SlaEventEntity> slaEventEntityList) {
        slaEventMapperService.saveBatch(slaEventEntityList);
        // TODO 数据通知
    }

    @Override
    public List<SlaEventEntity> check(List<MetricsInfo> metricsInfoList) {
        // TODO 进行校验
        return null;
    }
}
