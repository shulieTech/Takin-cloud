package io.shulie.takin.cloud.biz.service.engine.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.cloudserver.EnginePluginSimpleResultConvert;
import io.shulie.takin.cloud.biz.input.engine.EnginePluginWrapperInput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginDetailOutput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginFileOutput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginSimpleInfoOutput;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginFilesService;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginService;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginSupportedService;
import io.shulie.takin.cloud.common.constants.EnginePluginConstants;
import io.shulie.takin.cloud.common.constants.ResponseResultConstant;
import io.shulie.takin.cloud.data.mapper.mysql.EnginePluginMapper;
import io.shulie.takin.cloud.data.model.mysql.EnginePluginEntity;
import io.shulie.takin.cloud.data.result.engine.EnginePluginSimpleInfoResult;
import io.shulie.takin.common.beans.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 引擎接口实现
 *
 * @author lipeng
 * @date 2021-01-06 3:09 下午
 */
@Slf4j
@Service
public class EnginePluginServiceImpl extends ServiceImpl<EnginePluginMapper, EnginePluginEntity> implements EnginePluginService {

    @Resource
    private EnginePluginMapper enginePluginMapper;

    @Resource
    private EnginePluginSupportedService enginePluginSupportedService;

    @Resource
    private EnginePluginFilesService enginePluginFilesService;

    /**
     * 查询引擎支持的插件信息
     *
     * @param pluginTypes 插件类型
     *
     * @return -
     */
    @Override
    public Map<String, List<EnginePluginSimpleInfoOutput>> findEngineAvailablePluginsByType(List<String> pluginTypes) {
        Map<String, List<EnginePluginSimpleInfoOutput>> result = Maps.newHashMap();
        List<EnginePluginSimpleInfoResult> infos = enginePluginMapper.selectAvailablePluginsByType(pluginTypes);
        List<EnginePluginSimpleInfoOutput> outputInfos = EnginePluginSimpleResultConvert.INSTANCE.ofs(infos);
        if(CollectionUtils.isNotEmpty(outputInfos)) {
            result = outputInfos.stream().filter(r -> r.getPluginType() != null)
                    .collect(Collectors.groupingBy(EnginePluginSimpleInfoOutput::getPluginType));
        }
        return result;
    }

    /**
     * 根据插件ID获取插件详情
     *
     * @param pluginId
     * @return -
     */
    @Override
    public ResponseResult<EnginePluginDetailOutput> findEnginePluginDetails(Long pluginId) {
        //获取插件信息
        EnginePluginEntity enginePluginInfo = enginePluginMapper.selectById(pluginId);
        if(enginePluginInfo == null) {
            return ResponseResult.fail(ResponseResultConstant.RESPONSE_RESULT_CODE_ERROR, "引擎插件信息不存在，请核实");
        }
        //获取支持的版本信息
        List<String> supportedVersions = enginePluginSupportedService.findSupportedVersionsByPluginId(pluginId);
        //获取文件信息
        List<EnginePluginFileOutput> uploadFiles = enginePluginFilesService.findPluginFilesInfoByPluginId(pluginId);
        //返回结果
        return ResponseResult.success(EnginePluginDetailOutput.create(enginePluginInfo, supportedVersions, uploadFiles));
    }

    /**
     * 保存引擎插件
     *
     * @param input
     * @return -
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnginePlugin(EnginePluginWrapperInput input) {
        Long pluginId = input.getPluginId();
        boolean isEdit = (pluginId != null && pluginId != 0);
        //创建引擎插件实体
        EnginePluginEntity pressureEnginePluginEntity = isEdit
                ? enginePluginMapper.selectById(pluginId) : new EnginePluginEntity();

        BeanUtils.copyProperties(input, pressureEnginePluginEntity);
        LocalDateTime now = LocalDateTime.now();
        //保存引擎插件信息
        if(isEdit) {
            pressureEnginePluginEntity.setGmtUpdate(now);
            enginePluginMapper.updateById(pressureEnginePluginEntity);
        } else{
            //添加创建时间
            pressureEnginePluginEntity.setGmtCreate(now);
            pressureEnginePluginEntity.setGmtUpdate(now);
            pressureEnginePluginEntity.setStatus(EnginePluginConstants.ENGINE_PLUGIN_STATUS_ENABLED);
            enginePluginMapper.insert(pressureEnginePluginEntity);
        }

        //保存后插件id
        pluginId = pressureEnginePluginEntity.getId();

        //批量保存支持的版本信息
        //先删后增
        //先根据插件id移除支持的版本信息
        enginePluginSupportedService.removeSupportedVersionsByPluginId(pluginId);
        //再批量保存支持的版本信息
        enginePluginSupportedService.batchSaveSupportedVersions(input.getSupportedVersions(), pluginId);

        //将文件保存到NFS
        enginePluginFilesService.batchSaveEnginePluginFiles(input.getUploadFiles(), pluginId);
    }

    /**
     * @param pluginId
     * @param status
     * @return -
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEnginePluginStatus(Long pluginId, Integer status) {
        EnginePluginEntity enginePlugin = enginePluginMapper.selectById(pluginId);
        if(Objects.isNull(enginePlugin)) {
            log.warn("引擎插件信息不存在");
            return;
        }
        enginePlugin.setStatus(status);
        this.updateById(enginePlugin);
    }

}
