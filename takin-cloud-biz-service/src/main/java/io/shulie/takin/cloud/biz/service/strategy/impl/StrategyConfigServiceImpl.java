package io.shulie.takin.cloud.biz.service.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.strategy.TStrategyConfigMapper;
import com.pamirs.takin.entity.domain.dto.strategy.StrategyConfigDetailDTO;
import com.pamirs.takin.entity.domain.entity.strategy.StrategyConfig;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigAddVO;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigQueryVO;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigUpdateVO;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.common.enums.deployment.DeploymentMethodEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.DateUtil;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.ext.content.enginecall.StrategyOutputExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qianshui
 * @date 2020/5/9 下午3:17
 */
@Slf4j
@Service
public class StrategyConfigServiceImpl implements StrategyConfigService {

    @Resource
    private TStrategyConfigMapper tStrategyConfigMapper;

    @Autowired
    private EnginePluginUtils pluginUtils;

    @Override
    public Boolean add(StrategyConfigAddVO addVO) {
        StrategyConfig config = new StrategyConfig();
        config.setStrategyName(addVO.getStrategyName());
        config.setStrategyConfig(addVO.getStrategyConfig());
        tStrategyConfigMapper.insert(config);
        return true;
    }

    @Override
    public Boolean update(StrategyConfigUpdateVO updateVO) {
        StrategyConfig config = new StrategyConfig();
        config.setId(updateVO.getId());
        config.setStrategyName(updateVO.getStrategyName());
        config.setStrategyConfig(updateVO.getStrategyConfig());
        tStrategyConfigMapper.updateByPrimaryKeySelective(config);
        return true;
    }

    @Override
    public Boolean delete(Long id) {
        tStrategyConfigMapper.deleteByPrimaryKey(id);
        return true;
    }

    @Override
    public StrategyConfigDetailDTO getDetail(Long id) {
        StrategyConfig strategyConfig = tStrategyConfigMapper.selectByPrimaryKey(id);
        if (strategyConfig == null) {
            return null;
        }
        StrategyConfigDetailDTO dto = new StrategyConfigDetailDTO();
        dto.setStrategyName(strategyConfig.getStrategyName());
        dto.setStrategyConfig(strategyConfig.getStrategyConfig());
        return dto;
    }

    @Override
    public StrategyOutputExt getStrategy(Integer expectThroughput, Integer tpsNum) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        StrategyConfigExt strategyConfigExt = new StrategyConfigExt();
        strategyConfigExt.setThreadNum(expectThroughput);
        strategyConfigExt.setTpsNum(tpsNum);
        return engineCallExtApi.getPressureNodeNumRange(strategyConfigExt);

    }

    @Override
    public StrategyConfigExt getDefaultStrategyConfig() {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.getDefaultStrategyConfig();
    }

    @Override
    public PageInfo<StrategyConfigExt> queryPageList(StrategyConfigQueryVO queryVO) {
        Page page = PageHelper.startPage(queryVO.getCurrentPage() + 1, queryVO.getPageSize());

        List<StrategyConfig> queryList = tStrategyConfigMapper.getPageList(queryVO);
        if (CollectionUtils.isEmpty(queryList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<StrategyConfigExt> resultList = Lists.newArrayList();
        queryList.forEach(data -> {
            StrategyConfigExt dto = new StrategyConfigExt();
            dto.setId(data.getId());
            dto.setStrategyName(data.getStrategyName());
            parseConfig(dto, data.getStrategyConfig());
            dto.setUpdateTime(DateUtil.getYYYYMMDDHHMMSS(data.getUpdateTime()));
            resultList.add(dto);
        });

        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    private void parseConfig(StrategyConfigExt dto, String config) {
        try {
            JSONObject object = JSON.parseObject(config);
            dto.setThreadNum(object.getInteger("threadNum"));
            //默认2cpu
            BigDecimal cpuNum = object.getBigDecimal("cpuNum");
            dto.setCpuNum(cpuNum == null ? new BigDecimal(2) : cpuNum);
            //默认3G内存
            BigDecimal memorySize = object.getBigDecimal("memorySize");
            dto.setMemorySize(memorySize == null ? new BigDecimal(3072) : memorySize);
            //限制cpu 不填则默认为cpuNum
            BigDecimal limitCpuNum = object.getBigDecimal("limitCpuNum");
            dto.setLimitCpuNum(limitCpuNum == null ? cpuNum : limitCpuNum);
            //限制内存 不填默认为memorySize
            BigDecimal limitMemorySize = object.getBigDecimal("limitMemorySize");
            dto.setLimitMemorySize(limitMemorySize == null ? memorySize : limitMemorySize);
            dto.setTpsNum(object.getInteger("tpsNum"));
            dto.setDeploymentMethod(DeploymentMethodEnum.getByType(object.getInteger("deploymentMethod")));
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：解析配置失败 --> Parse Config Failure = {}，异常信息: {}",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR, config, e);
        }
    }
}
