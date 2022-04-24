package io.shulie.takin.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 任务实体
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_job_example")
public class JobExample {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("`name`")
    private String name;
}
