package com.pamirs.takin.entity.dao.report;

import java.util.Date;
import java.util.List;

import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.common.annotation.DataApartInterceptAnnotation;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import org.apache.ibatis.annotations.Param;

public interface TReportMapper {
    int insertSelective(Report record);

    @DataApartInterceptAnnotation
    Report selectOneRunningReport();

    @DataApartInterceptAnnotation
    List<Report> selectListRunningReport();

    int updateByPrimaryKeySelective(Report record);

    Report selectByPrimaryKey(Long id);

    int updateReportStatus(UpdateStatusBean updateStatus);


    int updateReportLock(UpdateStatusBean updateStatus);

    /**
     * 报表列表
     *
     * @param param
     * @return -
     */
    @DataApartInterceptAnnotation
    List<Report> listReport(@Param("param") ReportQueryParam param);

    /**
     * 获取已经生成报告的场景ID
     *
     * @param sceneIds
     * @return -
     */
    List<Report> listReportSceneIds(@Param("sceneIds") List<Long> sceneIds);

    /**
     * 根据场景ID获取压测中的报告ID
     *
     * @param sceneId
     * @return -
     */
    Report getReportBySceneId(Long sceneId);

    /**
     * 根据场景ID获取（临时）压测中的报告ID
     *
     * @param sceneId
     * @return -
     */
    Report getTempReportBySceneId(Long sceneId);

    int resumeStatus(Long sceneId);

    /**
     * 引擎启动，才更新开始时间
     *
     * @param id
     * @param startTime
     * @return -
     */
    int updateStartTime(@Param("id") Long id, @Param("startTime") Date startTime);


    int updateReportUserById(@Param("id") Long id, @Param("userId") Long userId);
}
