package io.shulie.takin.cloud.app.entity;

import java.util.Date;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 资源实例事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_resource_example_event")
public class ResourceExampleEventEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 资源实例主键
     */
    private Long resourceExampleId;
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
