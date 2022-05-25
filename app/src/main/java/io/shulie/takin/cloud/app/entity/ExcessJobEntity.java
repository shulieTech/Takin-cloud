package io.shulie.takin.cloud.app.entity;

import java.util.Date;

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
     * (主)任务主键
     */
    private Long jobId;
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
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 阈值时间
     */
    private Date thresholdTime;
}
