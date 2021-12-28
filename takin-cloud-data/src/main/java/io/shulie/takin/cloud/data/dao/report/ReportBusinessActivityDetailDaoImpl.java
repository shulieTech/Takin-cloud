package io.shulie.takin.cloud.data.dao.report;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.mapper.mysql.ReportBusinessActivityDetailMapper;

/**
 * @author moriarty
 */
@Service
public class ReportBusinessActivityDetailDaoImpl implements ReportBusinessActivityDetailDao {

    @Resource
    ReportBusinessActivityDetailMapper mapper;

    @Override
    public int insert(ReportBusinessActivityDetailEntity entity) {
        entity.setGmtCreate(new Date());
        entity.setGmtUpdate(new Date());
        return mapper.insert(entity);
    }

    @Override
    public int update(ReportBusinessActivityDetailEntity entity) {
        entity.setGmtUpdate(new Date());
        return mapper.updateById(entity);
    }

    @Override
    public ReportBusinessActivityDetailEntity selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<ReportBusinessActivityDetailEntity> selectDetailsByReportId(Long reportId) {
        LambdaQueryWrapper<ReportBusinessActivityDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportBusinessActivityDetailEntity::getReportId, reportId);
        wrapper.eq(ReportBusinessActivityDetailEntity::getIsDeleted, 0);
        return mapper.selectList(wrapper);
    }
}
