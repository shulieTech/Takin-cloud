package io.shulie.takin.cloud.app.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 调度器事件
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_watchman_event")
public class WatchmanEventEntity {
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
