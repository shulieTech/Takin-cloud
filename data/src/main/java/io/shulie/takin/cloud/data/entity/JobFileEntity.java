package io.shulie.takin.cloud.data.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 任务文件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_job_file")
public class JobFileEntity {
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
     * 任务实例主键
     */
    private Long jobExampleId;
    /**
     * 文件类型
     */
    private Integer type;
    /**
     * 统一资源标识符
     */
    private String uri;
    /**
     * 采样率
     */
    private Long startPoint;
    /**
     * 任务的运行模式
     */
    private Long endPoint;
    /**
     * 状态回调路径
     */
    private Date createTime;
}
