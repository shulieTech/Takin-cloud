package io.shulie.takin.cloud.biz.cloudserver;

import java.util.List;

import com.pamirs.takin.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.entity.domain.entity.scenemanage.WarnDetail;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.scenemanage.WarnDetailOutput;
import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import io.shulie.takin.cloud.common.utils.DateUtil;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.result.scenemanage.WarnDetailResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @Author 莫问
 * @Date 2020-04-17
 */

@Mapper(imports = {DateUtil.class})
public interface ReportConverter {

    ReportConverter INSTANCE = Mappers.getMapper(ReportConverter.class);

    /**
     * Report Converter ReportDetail
     *
     * @param report
     * @return
     */
    @Mapping(target = "startTime", expression = "java(DateUtil.getDate(report.getStartTime(),\"yyyy-MM-dd HH:mm:ss\"))")
    ReportDetailOutput ofReportDetail(ReportResult report);

    /**
     * Report Converter ReportDTO
     *
     * @param report
     * @return
     */
    List<CloudReportDTO> ofReport(List<Report> report);

    @Mappings({
        @Mapping(target = "startTime",
            expression = "java(DateUtil.getDate(report.getStartTime(),\"yyyy-MM-dd HH:mm:ss\"))"),
        @Mapping(target = "totalTime",
            expression = "java(DateUtil.formatTestTime(report.getStartTime(), report.getEndTime()))")
    })
    CloudReportDTO ofReport(Report report);

    /**
     * WarnBO Converter WarnBean
     *
     * @param warn
     * @return
     */
    List<WarnBean> ofWarn(List<WarnBO> warn);

    @Mappings(
            value = {
                    @Mapping(target = "reportId", source = "ptId"),
                    @Mapping(target = "content", source = "warnContent"),
                    @Mapping(target = "warnTime",
                            expression = "java(DateUtil.getDate(warnDetail.getWarnTime(),\"yyyy-MM-dd HH:mm:ss\"))")
            }
    )
    WarnDetailOutput ofWarn(WarnDetail warnDetail);

    /**
     * WarnDetail Converter WarnDetailResult
     *
     * @param warnDetail
     * @return
     */
    List<WarnDetailOutput> ofWarnDetail(List<WarnDetail> warnDetail);

    @Mapping(target = "lastWarnTime",
        expression = "java(DateUtil.getDate(warn.getLastWarnTime(),\"yyyy-MM-dd HH:mm:ss\"))")
    WarnBean ofWarn(WarnBO warn);

    @Mappings(
        value = {
            @Mapping(target = "reportId", source = "ptId"),
            @Mapping(target = "content", source = "warnContent"),
            @Mapping(target = "warnTime",
                expression = "java(DateUtil.getDate(warnDetail.getWarnTime(),\"yyyy-MM-dd HH:mm:ss\"))")
        }
    )
    WarnDetailResult ofWarnDetail(WarnDetail warnDetail);

    /**
     * ReportBusinessActivityDetail Converter BusinessActivityDTO
     *
     * @param data
     * @return
     */
    List<BusinessActivityDTO> ofBusinessActivity(List<ReportBusinessActivityDetail> data);
}
