package io.shulie.takin.cloud.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据校准任务 - 执行记录
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@TableName("t_calibration_log")
public class CalibrationLogEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 数据校准任务主键
     */
    private Long calibrationId;
    /**
     * 数据校准任务日志内容
     */
    private String content;
    /**
     * 是否完成
     */
    private Boolean completed;
}
