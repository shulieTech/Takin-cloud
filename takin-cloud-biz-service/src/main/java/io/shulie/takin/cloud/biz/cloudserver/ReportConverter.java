package io.shulie.takin.cloud.biz.cloudserver;

import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cn.hutool.core.date.DateUtil;

import com.pamirs.takin.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;

import io.shulie.takin.cloud.common.utils.TestTimeUtil;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;

/**
 * @author 莫问
 * @date 2020-04-17
 */

@Mapper(imports = {TestTimeUtil.class, DateUtil.class})
public interface ReportConverter {

    ReportConverter INSTANCE = Mappers.getMapper(ReportConverter.class);

    /**
     * Report Converter ReportDetail
     *
     * @param report -
     * @return -
     */
    @Mapping(target = "startTime", expression = "java(DateUtil.formatDateTime(report.getStartTime()))")
    ReportDetailOutput ofReportDetail(ReportEntity report);

    /**
     * WarnBO Converter WarnBean
     *
     * @param warn -
     * @return -
     */
    List<WarnBean> ofWarn(List<WarnBO> warn);

    /**
     * WarnDetail Converter WarnDetailResult
     *
     * @param warnDetail -
     * @return -
     */
    List<WarnDetailOutput> ofWarnDetail(List<WarnDetail> warnDetail);

    /**
     * io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity Converter BusinessActivityDTO
     *
     * @param data -
     * @return -
     */
    List<BusinessActivityDTO> ofBusinessActivity(List<ReportBusinessActivityDetailEntity> data);
}
