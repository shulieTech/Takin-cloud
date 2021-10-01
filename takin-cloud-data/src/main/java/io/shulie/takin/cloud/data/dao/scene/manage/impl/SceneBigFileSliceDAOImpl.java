package io.shulie.takin.cloud.data.dao.scene.manage.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.takin.entity.dao.scene.manage.TSceneScriptRefMapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import io.shulie.takin.cloud.common.enums.FileSliceStatusEnum;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneBigFileSliceDAO;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBigFileSliceMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneScriptRefMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import io.shulie.takin.cloud.data.util.MPUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author moriarty
 */
@Component
public class SceneBigFileSliceDAOImpl extends ServiceImpl<SceneBigFileSliceMapper, SceneBigFileSliceEntity>
    implements SceneBigFileSliceDAO, MPUtil<SceneBigFileSliceEntity> {

    private static Logger logger = LoggerFactory.getLogger(SceneBigFileSliceDAOImpl.class);

    @Resource
    SceneScriptRefMapper sceneScriptRefMapper;

    @Resource
    TSceneScriptRefMapper tSceneScriptRefMapper;

    @Resource
    SceneBigFileSliceMapper sceneBigFileSliceMapper;

    private static final String SUFFIX = ".csv";

    @Override
    public int create(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneScriptRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneScriptRefEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneScriptRefEntity::getId, param.getFileRefId());
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(wrapper);
        if (sceneScriptRefEntities.size() == 1) {
            SceneScriptRefEntity entity = sceneScriptRefEntities.get(0);
            if (entity.getFileName().endsWith(SUFFIX)) {
                SceneBigFileSliceEntity sliceEntity = new SceneBigFileSliceEntity() {{
                    setFileName(entity.getFileName());
                    setFilePath(entity.getUploadPath());
                    setSceneId(param.getSceneId());
                    setStatus(param.getStatus());
                    setScriptRefId(entity.getId());
                    setSliceCount(param.getSliceCount());
                    setSliceInfo(param.getSliceInfo());
                    setFileUpdateTime(param.getFileUploadTime());
                    setCreateTime(LocalDateTime.now());
                }};
                return sceneBigFileSliceMapper.insert(sliceEntity);
            }
        }
        return 0;
    }

    @Override
    public int isFileSliced(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneScriptRefEntity> refWrapper = new LambdaQueryWrapper<>();
        refWrapper.eq(SceneScriptRefEntity::getSceneId, param.getSceneId());
        refWrapper.eq(SceneScriptRefEntity::getFileName, param.getFileName());
        //文件类型有多种，只查类型为1的数据文件
        refWrapper.eq(SceneScriptRefEntity::getFileType, 1);
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(refWrapper);
        List<SceneScriptRefEntity> collect = sceneScriptRefEntities
            .stream()
            .filter(sceneScriptRefEntity ->
                sceneScriptRefEntity.getFileName().endsWith(SUFFIX)
                    && sceneScriptRefEntity.getIsDeleted() == 0)
            .collect(Collectors.toList());
        if (collect.size() == 1) {
            SceneScriptRefEntity entity = collect.get(0);
            if (entity != null && entity.getSceneId().equals(param.getSceneId())) {
                LambdaQueryWrapper<SceneBigFileSliceEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SceneBigFileSliceEntity::getSceneId, param.getSceneId());
                wrapper.eq(SceneBigFileSliceEntity::getFileName, param.getFileName());
                SceneBigFileSliceEntity sliceEntity = sceneBigFileSliceMapper.selectOne(wrapper);
                if (sliceEntity != null) {
                    if (Objects.isNull(sliceEntity.getSliceCount())
                        || StringUtils.isBlank(sliceEntity.getSliceInfo())) {
                        return FileSliceStatusEnum.FILE_CHANGED.getCode();
                    }
                    if (entity.getFileName().equals(param.getFileName())) {
                        long scriptRefUploadTime = entity.getUploadTime().getTime();
                        if (Objects.isNull(sliceEntity.getFileUpdateTime())) {
                            return FileSliceStatusEnum.FILE_CHANGED.getCode();
                        }
                        long sliceUploadTime = sliceEntity.getFileUpdateTime().getTime();
                        if (sliceUploadTime == scriptRefUploadTime) {
                            return FileSliceStatusEnum.SLICED.getCode();
                        } else {
                            logger.error("时间不匹配，slice time is [{}], scriptRef time is [{}]", sliceUploadTime,
                                scriptRefUploadTime);
                            return FileSliceStatusEnum.FILE_CHANGED.getCode();
                        }
                    }
                    return FileSliceStatusEnum.FILE_CHANGED.getCode();
                }
            }
        }
        return FileSliceStatusEnum.UNSLICED.getCode();
    }

    @Override
    public boolean isFileNeedSlice(Long refId) {
        SceneScriptRefEntity entity = sceneScriptRefMapper.selectById(refId);
        if (Objects.nonNull(entity)) {
            String fileExtend = entity.getFileExtend();
            JSONObject jsonObject = JSONObject.parseObject(fileExtend);
            if (jsonObject.containsKey("isOrderSplit")) {
                return "1".equals(jsonObject.getString("isOrderSplit"));
            }
            return false;
        }
        return false;
    }

    @Override
    public int update(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneBigFileSliceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneBigFileSliceEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneBigFileSliceEntity::getFileName, param.getFileName());
        SceneBigFileSliceEntity entity = sceneBigFileSliceMapper.selectOne(wrapper);
        if (Objects.isNull(entity) || Objects.isNull(entity.getId())) {
            return 0;
        }
        SceneScriptRefEntity entity1 = this.selectRef(param);
        entity.setSliceCount(param.getSliceCount());
        entity.setScriptRefId(param.getFileRefId());
        entity.setFilePath(entity1.getUploadPath());
        entity.setFileName(param.getFileName());
        entity.setFileUpdateTime(param.getFileUploadTime());
        entity.setSliceInfo(param.getSliceInfo());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setStatus(param.getStatus());
        return sceneBigFileSliceMapper.updateById(entity);
    }

    @Override
    public SceneScriptRefEntity selectRef(SceneBigFileSliceParam param) {
        if (Objects.nonNull(param.getFileRefId()) && param.getFileRefId() > 0) {
            return sceneScriptRefMapper.selectById(param.getFileRefId());
        }
        LambdaQueryWrapper<SceneScriptRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneScriptRefEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneScriptRefEntity::getFileName, param.getFileName());
        //只查type=1的文件
        wrapper.eq(SceneScriptRefEntity::getFileType, 1);
        return sceneScriptRefMapper.selectOne(wrapper);
    }

    @Override
    public SceneBigFileSliceEntity selectOne(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneBigFileSliceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneBigFileSliceEntity::getSceneId, param.getSceneId());
        if (StringUtils.isNotBlank(param.getFileName())) {
            wrapper.eq(SceneBigFileSliceEntity::getFileName, param.getFileName());
        }
        List<SceneBigFileSliceEntity> sceneBigFileSliceEntities = sceneBigFileSliceMapper.selectList(wrapper);
        if (sceneBigFileSliceEntities.size() >= 1) {
            return sceneBigFileSliceEntities.get(0);
        }
        return null;
    }

    @Override
    public Long createRef(SceneScriptRef ref) {
        return tSceneScriptRefMapper.insertSelective(ref);
    }

    @Override
    public int updateRef(SceneScriptRefEntity entity) {
        SceneScriptRefEntity entity1 = sceneScriptRefMapper.selectById(entity.getId());
        entity1.setFileExtend(entity.getFileExtend());
        return sceneScriptRefMapper.updateById(entity1);
    }
}
