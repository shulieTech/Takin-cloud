package io.shulie.takin.cloud.app.service;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.data.entity.ExcessJobEntity;

/**
 * 定时任务 - 服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface ExcessJobService {

    /**
     * 列出需要运行的任务
     *
     * @param pageNumber  分页页码
     * @param pageSize    分页容量
     * @param type        任务类型
     * @param isCompleted 是否完成
     * @return 分页后的任务列表
     */
    PageInfo<ExcessJobEntity> list(int pageNumber, int pageSize, Integer type, boolean isCompleted);

    /**
     * 列出没有完成的额外任务
     *
     * @param pageNumber 分页页码
     * @param pageSize   分页容量
     * @param type       任务类型
     * @return 分页后的任务列表
     */
    default PageInfo<ExcessJobEntity> listNotCompleted(int pageNumber, int pageSize, Integer type) {
        return list(pageNumber, pageSize, type, false);
    }

    /**
     * 创建定时任务
     *
     * @param type    类型
     * @param jobId   (主)任务主键
     * @param content 内容
     * @return 定时任务
     */
    Long create(int type, long jobId, String content);

    /**
     * 录入任务记录
     *
     * @param scheduleId  任务主键
     * @param content     运行内容
     * @param isCompleted 是否完成
     * @return 记录主键
     */
    Long log(long scheduleId, String content, boolean isCompleted);

    /**
     * 执行调度
     *
     * @param entity 调度内容
     */
    void exec(ExcessJobEntity entity);

    /**
     * 数据校准
     *
     * @param entity 额外任务
     * @return 校准结果
     */
    boolean calibration(ExcessJobEntity entity);
}
