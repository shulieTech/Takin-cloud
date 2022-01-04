package io.shulie.takin.cloud.data.dao.pressure.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.dao.BaseDao;
import io.shulie.takin.cloud.data.dao.pressure.PressureTaskDao;
import io.shulie.takin.cloud.data.mapper.mysql.PressureTaskMapper;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.param.pressure.PressureTaskQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * @Author: liyuanba
 * @Date: 2021/12/28 3:37 下午
 */
@Slf4j
@Service
public class PressureTaskDaoImpl extends BaseDao implements PressureTaskDao {
    @Resource
    private PressureTaskMapper mapper;

    @Override
    public int insert(PressureTaskEntity entity) {
        if (null == entity) {
            log.error("inset 错误，entity对象为null");
            return 0;
        }
        return mapper.insert(entity);
    }

    @Override
    public int update(PressureTaskEntity entity) {
        if (null == entity) {
            log.error("update 错误，entity对象为null");
            return 0;
        }
        if (null == entity.getId()) {
            log.error("update 错误，entity对象的ID为null，entity="+ JsonUtil.toJson(entity));
            return 0;
        }
        return mapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        if (null == id) {
            return 0;
        }
        return mapper.deleteById(id);
    }

    @Override
    public PressureTaskEntity getById(Long id) {
        if (null == id) {
            return null;
        }
        return mapper.selectById(id);
    }

    @Override
    public PressureTaskQueryParam query(PressureTaskQueryParam param) {
        initPage(param);
        LambdaQueryWrapper<PressureTaskEntity> wrapper = new LambdaQueryWrapper<>();
        if (null != param.getId()) {
            wrapper.eq(PressureTaskEntity::getId, param.getId());
        }
        if (null != param.getTenantId()) {
            wrapper.eq(PressureTaskEntity::getTenantId, param.getTenantId());
        }
        if (StringUtils.isNotBlank(param.getEnvCode())) {
            wrapper.eq(PressureTaskEntity::getEnvCode, param.getEnvCode());
        }
        if (null != param.getSceneId()) {
            wrapper.eq(PressureTaskEntity::getSceneId, param.getSceneId());
        }
        if (null != param.getSceneType()) {
            wrapper.eq(PressureTaskEntity::getSceneType, param.getSceneType().getCode());
        }
        if (null != param.getStatus()) {
            wrapper.eq(PressureTaskEntity::getStatus, param.getStatus());
        }
        if (CollectionUtils.isNotEmpty(param.getStatuses())) {
            wrapper.in(PressureTaskEntity::getStatus, param.getStatuses());
        }
        if (null != param.getAdminId()) {
            wrapper.eq(PressureTaskEntity::getAdminId, param.getAdminId());
        }
        wrapper.orderByDesc(PressureTaskEntity::getId);
        return mapper.selectPage(param, wrapper);
    }

    public int updateStatus(Long id, Integer status) {
        if (null == id) {
            log.error("updateStatus id is null!");
            return 0;
        }
        if (null == status) {
            log.error("updateStatus status is null!");
            return 0;
        }
        PressureTaskEntity entity = new PressureTaskEntity();
        entity.setId(id);
        entity.setStatus(status);
        if (1 == status) {
            entity.setGmtStart(Calendar.getInstance().getTime());
        } else if (2 == status) {
            entity.setGmtEnd(Calendar.getInstance().getTime());
        }
        return update(entity);
    }
}
