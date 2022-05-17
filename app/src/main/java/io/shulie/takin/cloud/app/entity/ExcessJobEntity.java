package io.shulie.takin.cloud.app.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 额外的任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_excess_job")
public class ExcessJobEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 任务类型
     */
    private Integer type;
    /**
     * 任务内容
     */
    private String content;
    /**
     * 是否完成
     */
    private Boolean completed;
}
