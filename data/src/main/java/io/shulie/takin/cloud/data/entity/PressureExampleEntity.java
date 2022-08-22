package io.shulie.takin.cloud.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 施压任务实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_pressure_example")
public class PressureExampleEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 施压任务主键
     */
    private Long pressureId;
    /**
     * 资源实例主键
     */
    private Long resourceExampleId;
    /**
     * 序列号
     */
    private Integer number;
    /**
     * 持续时长
     */
    private Integer duration;
}
