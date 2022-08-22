package io.shulie.takin.cloud.app.service;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.data.entity.CalibrationEntity;

/**
 * 数据校准任务 - 服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface CalibrationService {

    /**
     * 列出需要运行的任务
     *
     * @param pageNumber  分页页码
     * @param pageSize    分页容量
     * @param isCompleted 是否完成
     * @return 分页后的任务列表
     */
    PageInfo<CalibrationEntity> list(int pageNumber, int pageSize, boolean isCompleted);

    /**
     * 列出没有完成的额外任务
     *
     * @param pageNumber 分页页码
     * @param pageSize   分页容量
     * @return 分页后的任务列表
     */
    default PageInfo<CalibrationEntity> listNotCompleted(int pageNumber, int pageSize) {
        return list(pageNumber, pageSize, false);
    }

    /**
     * 创建定时任务
     *
     * @param pressureId 施压任务主键
     * @return 定时任务
     */
    Long create(long pressureId);

    /**
     * 录入任务记录
     *
     * @param calibrationId 数据校准任务主键
     * @param content       数据校准任务运行结果
     * @param isCompleted   是否完成
     * @return 记录主键
     */
    Long log(long calibrationId, String content, boolean isCompleted);

    /**
     * 执行调度
     *
     * @param entity 调度内容
     */
    void exec(CalibrationEntity entity);

}
