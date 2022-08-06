package io.shulie.takin.cloud.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 数据库实体隐射 - 施压任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@TableName("t_pressure")
@Accessors(chain = true)
public class PressureEntity {
    /**
     * 数据主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 施压任务名称
     */
    private String name;
    /**
     * 资源主键
     */
    private Long resourceId;
    /**
     * 持续时长
     */
    private Integer duration;
    /**
     * 采样率
     */
    private Integer sampling;
    /**
     * 运行模式
     */
    private Integer type;
    /**
     * 状态回调路径
     */
    private String callbackUrl;
    /**
     * 资源实例数量
     */
    private Integer resourceExampleNumber;
    /**
     * 启动选项
     */
    private String startOption;

}
