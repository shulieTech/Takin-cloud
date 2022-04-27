package io.shulie.takin.cloud.app.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据库实体隐射 - 任务实例事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_job_example_event")
public class JobExampleEventEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 任务实例主键
     */
    private Long jobExampleId;
    /**
     * 事件上报时间
     */
    private Date time;
    /**
     * 事件类型
     */
    private Integer type;
    /**
     * 事件内容
     */
    private String context;
}
