package io.shulie.takin.cloud.app.service;

import java.util.List;

import io.shulie.takin.cloud.app.entity.JobEntity;
import io.shulie.takin.cloud.model.response.JobConfig;
import io.shulie.takin.cloud.model.request.StartRequest;
import io.shulie.takin.cloud.app.entity.JobExampleEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 任务服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface JobService {

    /**
     * 启动
     *
     * @param jobInfo 任务信息
     * @return 任务主键
     * @throws JsonProcessingException JSON异常
     */
    String start(StartRequest jobInfo) throws JsonProcessingException;

    /**
     * 停止
     *
     * @param jobId 任务主键
     */
    void stop(long jobId);

    /**
     * 查看配置
     *
     * @param jobId 任务主键
     * @param ref   关键词
     * @return 配置内容
     */
    List<JobConfig> getConfig(long jobId, String ref);

    /**
     * 修改配置
     *
     * @param jobId   任务主键
     * @param context 配置内容
     */
    void modifyConfig(long jobId, JobConfig context);

    /**
     * 获取数据对象 - 任务
     *
     * @param jobId 任务主键
     * @return Entity
     */
    JobEntity jobEntity(long jobId);

    /**
     * 获取数据对象 - 任务实例
     *
     * @param jobExampleId 任务实例主键
     * @return Entity
     */
    JobExampleEntity jobExampleEntity(long jobExampleId);
}
