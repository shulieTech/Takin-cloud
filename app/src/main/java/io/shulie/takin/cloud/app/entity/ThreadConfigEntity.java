package io.shulie.takin.cloud.app.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 线程组配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_thread_config")
public class ThreadConfigEntity {
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
     * 关键字
     */
    private String ref;
    /**
     * 关键字
     */
    private Integer mode;
    /**
     * 配置内容
     */
    private String context;
}
