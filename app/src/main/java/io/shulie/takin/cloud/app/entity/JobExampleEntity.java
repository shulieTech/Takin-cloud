package io.shulie.takin.cloud.app.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_job_example")
public class JobExampleEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 任务主键
     */
    private Long jobId;
    /**
     * 资源实例主键
     */
    private Long resourceExampleId;
    /**
     * 任务持续时长
     */
    private Long duration;
}
