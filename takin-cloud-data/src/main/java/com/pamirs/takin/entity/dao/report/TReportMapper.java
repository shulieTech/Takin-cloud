package com.pamirs.takin.entity.dao.report;

import java.util.Date;
import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.common.annotation.DataApartInterceptAnnotation;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.report.ReportQueryReq;
import org.apache.ibatis.annotations.Param;

/**
 * @author -
 */
public interface TReportMapper {

    /**
     * 查询一个运行中的报告
     *
     * @return 压测报告
     */
    @DataApartInterceptAnnotation
    ReportEntity selectOneRunningReport(ContextExt contextExt);

    /**
     * 查询所有运行中的报告
     *
     * @return 压测报告列表
     */
    @DataApartInterceptAnnotation
    List<ReportEntity> selectListRunningReport(ContextExt contextExt);

    List<ReportEntity> selectListPressuringReport();

    /**
     * 依据主键更新
     *
     * @param record 要更新的结果(包含主键)
     * @return 操作结果
     */
    @DataApartInterceptAnnotation
    int updateByPrimaryKeySelective(ReportEntity record);

    /**
     * 根据主键查询
     *
     * @param id 数据主键
     * @return 报告信息
     */
    ReportEntity selectByPrimaryKey(Long id);

    /**
     * 更新报告状态
     *
     * @param updateStatus 报告状态
     * @return 操作结果
     */
    int updateReportStatus(UpdateStatusBean updateStatus);

    /**
     * 更新报告锁信息
     *
     * @param updateStatus 锁状态
     * @return 操作结果
     */
    int updateReportLock(UpdateStatusBean updateStatus);

    /**
     * 报表列表
     *
     * @param param 入参
     * @return -
     */
    @DataApartInterceptAnnotation
    List<ReportEntity> listReport(@Param("param") ReportQueryReq param);

    /**
     * 获取已经生成报告的场景ID
     *
     * @param sceneIds 场景主键集合
     * @return -
     */
    List<ReportEntity> listReportSceneIds(@Param("sceneIds") List<Long> sceneIds);

    /**
     * 根据场景ID获取压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportEntity getReportBySceneId(Long sceneId);

    /**
     * 根据场景ID获取（临时）压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportEntity getTempReportBySceneId(Long sceneId);

    int resumeStatus(Long sceneId);

    /**
     * 引擎启动，才更新开始时间
     *
     * @param id        场景主键
     * @param startTime 开始时间
     * @return -
     */
    int updateStartTime(@Param("id") Long id, @Param("startTime") Date startTime);

    int updateReportUserById(@Param("id") Long id, @Param("userId") Long userId);
}
