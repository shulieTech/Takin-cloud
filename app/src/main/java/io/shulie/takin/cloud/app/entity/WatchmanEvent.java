package io.shulie.takin.cloud.app.entity;

import java.util.Date;
import java.util.HashMap;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

/**
 * 数据库实体隐射 - 调度器事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_watchman_event")
public class WatchmanEvent {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 调度器主键
     */
    private Long watchmanId;
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
