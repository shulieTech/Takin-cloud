package io.shulie.takin.cloud.data.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 线程组配置实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_thread_config_example")
public class ThreadConfigExampleEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分隔索引
     */
    private Integer serialNumber;
    /**
     * 任务主键
     */
    private Long jobId;
    /**
     * 任务实例主键
     */
    private Long jobExampleId;
    /**
     * 关键字
     */
    private String ref;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 配置内容
     */
    private String context;
    /**
     * 修改时间
     */
    private Date updateTime;
}
